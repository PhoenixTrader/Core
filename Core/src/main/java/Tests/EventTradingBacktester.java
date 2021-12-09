package Tests;

import java.io.FileReader;
import java.time.ZonedDateTime;
import java.util.List;

import javax.swing.plaf.synth.SynthSplitPaneUI;

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
import Strategies.EventTrigger.EventTrading;
import Infrastructure.Order.Order;

public class EventTradingBacktester {
    

    public static void BackTestEvent() {
        // -------------- parameter definition ----------
        String underlying = "BTCUSD"; // "EURCHF"; test / 1
        String ticks = "1"; // "5"; 
        String frame = "minutely";

        String fileID = "Test000001";
        String[] assetClasses = { "FX" };
        String[] symbols = { underlying };
        QuoteDataFeed quoteDataFeed = new QuoteDataFeed(assetClasses, symbols, new ConnectorGeneric());

        ArrayList<String> underlyingList = new ArrayList<String>();
        underlyingList.add(underlying);
        OrderManagement orderManagement = new OrderManagement(underlyingList);

        AccountManagement accountManagement = new AccountManagement(10000);
        EventTrading EventTrading = new EventTrading();
        EventTrading.setUnderlying(underlying);

        int tickLimit = 1000000000;
        double spread = 0.00000001;
        // -----------------------------

        String file = "D:/MonkeyTrading/HistoricalData/FX/" + frame + "/" + underlying + ticks + ".csv"; // D:/MonkeyTrading/HistoricalData/Equity/minutely/test.csv
        int currentTick = 0;

        CSVReader csvReader = null;

        try {

            FileReader filereader = new FileReader(file);

            csvReader = new CSVReader(filereader);
            String[] nextRecord;

            nextRecord = csvReader.readNext();
            Quote firstQuote = CandleStick.CandleStickFromString(underlying, nextRecord).GetCloseQuote(spread);
            quoteDataFeed.RefreshStatic("FX", firstQuote);

            Map<String, ArrayList<Order>> ordersFromStrategy;
            while ((nextRecord = csvReader.readNext()) != null) {
                System.out.print(csvReader.readNext());
                if (currentTick >= tickLimit) {
                    // break;
                }

                ArrayList<String> eventList = new ArrayList<String>();;
                eventList.add("Construction PMI");

                for (Quote quote : CandleStick.CandleStickFromString(underlying, nextRecord).Expand(4, spread)) {

                    quote.SetSymbol(underlying);

                    System.out.print(quote.Print() + "\n");

                    quoteDataFeed.RefreshStatic("FX", quote);

                    ordersFromStrategy = EventTrading.Trade(orderManagement, quoteDataFeed, quote, eventList);
					if (ordersFromStrategy != null) {
                        System.out.println(ordersFromStrategy);
                        orderManagement.FillAndFetch("FX", ordersFromStrategy);

                        accountManagement.FetchData(orderManagement, quoteDataFeed);
                    }

                }
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