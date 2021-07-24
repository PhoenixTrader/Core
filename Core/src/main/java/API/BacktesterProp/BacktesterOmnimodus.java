package API.BacktesterProp;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.*;
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

import Strategies.*;

public class BacktesterOmnimodus {

    public iStrategy strategy;

    public BacktesterOmnimodus(iStrategy inputStrategy, JSONObject parameters){
        this.strategy = inputStrategy.Initialize(parameters);
    }

    public void RunBacktest(){};
}
