package Tests;

import java.io.FileReader;
import java.time.ZonedDateTime;
import java.util.List;

import java.util.ArrayList;
import java.util.*;

import com.opencsv.CSVReader;

import DataLoader.CandleStick.CandleStick;
import DataFeed.CalendarFeed.ForexFactoryCalendarData;
import DataFeed.Connector.ConnectorGeneric;
import DataFeed.QuoteDataFeed.QuoteDataFeed;

import Infrastructure.AccountManagement.*;
import Infrastructure.OrderManagement.*;
import Infrastructure.Quote.Quote;
import Infrastructure.Order.Order;
//import Infrastructure.Order.*;

import Strategies.AlphaEnginePlus.*;

public class BacktesterCalendarStyle {

    public static void BackTestCal() {
        // -------------- parameter definition ----------
        String underlying = "BTCUSD"; // "EURCHF"; test / 1
        String ticks = "1"; // "5"; 
        String frame = "minutely";
        double deltaArray[] = { 0.00125, 0.00175, 0.005 };
        double deltaOvershootScale[] = { 1.0, 1.0, 1.0 };

        double positionSizeArray[] = { 0.05, 0.5, 0.1 };
        double exposureBarrierLevels[] = { 0.5, 0.75, 1.0, 2.0, 4.0 };
        String measureThresholds = "lin";
        String fileID = "Test000001";
        String[] assetClasses = { "FX" };
        String[] symbols = { underlying };
        QuoteDataFeed quoteDataFeed = new QuoteDataFeed(assetClasses, symbols, new ConnectorGeneric());

        ArrayList<String> underlyingList = new ArrayList<String>();
        underlyingList.add(underlying);
        OrderManagement orderManagement = new OrderManagement(underlyingList);

        AccountManagement accountManagement = new AccountManagement(10000);
        AlphaEnginePlus aePlus = new AlphaEnginePlus(underlying, deltaArray, deltaOvershootScale, positionSizeArray,
                exposureBarrierLevels, measureThresholds, fileID, quoteDataFeed);

        int tickLimit = 1000000000;
        // -----------------------------

        double spread = 0.00000001;
        // CSVParser parser = new
        // CSVParserBuilder().withSeparator(',').withIgnoreQuotations(true).build();
        // D:\MonkeyTrading\HistoricalData\FX

        // Start Part 1/3 -  Calendar variables
        ForexFactoryCalendarData FFCD = new ForexFactoryCalendarData();
        FFCD.SetWebsite("https://www.forexfactory.com");
        // End Part 1/3 -  Calendar variables

        String file = "D:/MonkeyTrading/HistoricalData/FX/" + frame + "/" + underlying + ticks + ".csv"; // D:/MonkeyTrading/HistoricalData/Equity/minutely/test.csv
        int currentTick = 0;

        CSVReader csvReader = null;

        try {

            FileReader filereader = new FileReader(file);

            csvReader = new CSVReader(filereader);
            String[] nextRecord;
            // Quote[] quoteList;

            nextRecord = csvReader.readNext();
            Quote firstQuote = CandleStick.CandleStickFromString(underlying, nextRecord).GetCloseQuote(spread);
            quoteDataFeed.RefreshStatic("FX", firstQuote);
            aePlus.FetchQuoteData(quoteDataFeed);

            // Start Part 2/3 Calendar - Get websitename for calendar
            FFCD.SetQuote(firstQuote);
            String sCurrentCalWebsite = FFCD.GetFullWebsiteNameBasedOnDate(firstQuote.GetTime());
            // End Part 2/3 Calendar

            Map<String, ArrayList<Order>> ordersFromStrategy;
            while ((nextRecord = csvReader.readNext()) != null) {
                System.out.print(csvReader.readNext());
                if (currentTick >= tickLimit) {
                    // break;
                }

                for (Quote quote : CandleStick.CandleStickFromString(underlying, nextRecord).Expand(4, spread)) {

                    quote.SetSymbol(underlying);

                    // System.out.print(quote.Print() + "\n");

                    quoteDataFeed.RefreshStatic("FX", quote);

                    // quoteDataFeed.PrintFXLiveQuotes();

                    // Start Part 3/3 Calendar - Look for time match with datafeed
                    String sNewCalWebsite = FFCD.GetFullWebsiteNameBasedOnDate(quote.GetTime());

                    if (sCurrentCalWebsite.equals(sNewCalWebsite) != true || FFCD.fullHTMLPage == null) {
                        FFCD.SetQuote(quote);
                        FFCD.lCalData();
                        sCurrentCalWebsite = FFCD.GetFullWebsiteNameBasedOnDate(quote.GetTime());
                    } else {
                        System.out.println("Did not fetch calendar");
                    }
                    List<ZonedDateTime> zonedDateTimesCalendar = FFCD
                            .zdtCalendar(quote.GetTime().toLocalDate(), FFCD.GetTimeList()); //"Europe/Berlin"                 
                    
                    if (zonedDateTimesCalendar.contains(quote.GetTime())) {
                        System.out.println("It is in: " + "------" + quote.GetTime());
                        System.out.println("BREAK");
                    } else {
                        System.out.println("Not in ---- " + quote.GetTime());
                        System.out.println("Not in ---- " + FFCD.daysTime);
                    }
                    // End Part 3/3 Calendar - Look for time match with datafeed

                    ordersFromStrategy = aePlus.Trade(orderManagement, quoteDataFeed);

                    // if(!ordersFromStrategy.get(underlying).isEmpty())
                    // System.out.print("stop");
                    orderManagement.FillAndFetch("FX", ordersFromStrategy);

                    accountManagement.FetchData(orderManagement, quoteDataFeed);
                }
                /*
                 * if( currentTick % 1000000000 ==0 ) {
                 * System.out.print("----------- status -------------\n");
                 * 
                 * orderManagement.Print();
                 * 
                 * accountManagement.Print();
                 * 
                 * System.out.print("----------- status end -------------\n"); }
                 * 
                 * currentTick += 1;
                 * 
                 * System.out.println();
                 */
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Error in Loop!\n");
        }

        System.out.print("----------- Final status -------------\n");

        orderManagement.Print();

        accountManagement.Print();

        System.out.print("----------- status end -------------\n");

        System.out.print("Execution finished!\n");
        try {
            csvReader.close();
        } catch (Exception e) {

        }
    }

}