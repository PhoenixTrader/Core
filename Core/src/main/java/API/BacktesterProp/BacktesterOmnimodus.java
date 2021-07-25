package API.BacktesterProp;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.*;
import java.time.ZonedDateTime;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import DataLoader.CandleStick.CandleStick;
import DataFeed.Connector.ConnectorGeneric;
import DataFeed.QuoteDataFeed.QuoteDataFeed;

import Infrastructure.AccountManagement.*;
import Infrastructure.OrderManagement.*;
import Infrastructure.Quote.Quote;
import Infrastructure.Order.Order;
import Infrastructure.Order.*;

import API.BacktesterProp.*;

import Strategies.*;

public class BacktesterOmnimodus {

    public iStrategy strategy;
    public OrderManagement orderManagement;
    public QuoteDataFeed quoteDataFeed;
    public AccountManagement accountManagement;
    public ArrayList<String> underlyings;

    BacktesterConfig backtesterConfig;

    String file;

    public BacktesterOmnimodus(JSONObject config, iStrategy inputStrategy, JSONObject parameters, ZonedDateTime start,
            ZonedDateTime end) {
        this.strategy = inputStrategy.Initialize(parameters);

        this.backtesterConfig = new BacktesterConfig(config);

        this.quoteDataFeed = new QuoteDataFeed((String[]) this.strategy.GetAssetClasses().toArray(),
                (String[]) this.strategy.GetUnderlyings().toArray(), new ConnectorGeneric());

        this.underlyings = new ArrayList<String>();

        this.orderManagement = new OrderManagement(this.strategy.GetUnderlyings());

        this.accountManagement = new AccountManagement(10000);

        // CSVParser parser = new
        // CSVParserBuilder().withSeparator(',').withIgnoreQuotations(true).build();

        this.file = "path/toFile.csv"; // D:/MonkeyTrading/HistoricalData/Equity/minutely/test.csv

    }

    public void RunBacktest() {

        try {

            FileReader filereader = new FileReader(this.file);

            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            Quote[] quoteList;

            nextRecord = csvReader.readNext();
            Quote firstQuote = CandleStick.CandleStickFromString(this.underlyings.get(0), nextRecord)
                    .GetCloseQuote(this.backtesterConfig.GetSpread());
            quoteDataFeed.RefreshStatic("FX", firstQuote);

            this.strategy.FetchQuoteData(quoteDataFeed);

            int currentTick = 0;
            int tickLimit = 100000;

            Map<String, ArrayList<Order>> ordersFromStrategy;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (currentTick >= tickLimit) {
                    break;
                }
                for (Quote quote : CandleStick.CandleStickFromString(this.underlyings.get(0), nextRecord).Expand(4,
                        this.backtesterConfig.GetSpread())) {

                    quote.SetSymbol(this.underlyings.get(0));

                    // System.out.print(quote.Print() + "\n");

                    quoteDataFeed.RefreshStatic("FX", quote);

                    // quoteDataFeed.PrintFXLiveQuotes();

                    ordersFromStrategy = strategy.Trade(orderManagement, quoteDataFeed);

                    if (!ordersFromStrategy.get(this.underlyings.get(0)).isEmpty())
                        System.out.print("stop");
                    orderManagement.FillAndFetch("FX", ordersFromStrategy);

                    accountManagement.FetchData(orderManagement, quoteDataFeed);
                }

                if (currentTick % 1000000000 == 0) {
                    System.out.print("----------- status -------------\n");

                    orderManagement.Print();

                    accountManagement.Print();

                    System.out.print("----------- status end -------------\n");
                }

                currentTick += 1;

                System.out.println();
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
    };

    public void ParseConfig(JSONObject config) {

    }
}
