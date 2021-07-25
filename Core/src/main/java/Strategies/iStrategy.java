package Strategies;

import DataFeed.QuoteDataFeed.*;
import Infrastructure.OrderManagement.*;

import Strategies.Measures.*;
import Infrastructure.Order.Order;

import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.util.*;

import org.json.JSONObject;

public interface iStrategy {

    // the trade function, returns a dictionary with symbols as keys and respective orders in lists as values
    public Map<String, ArrayList<Order>> Trade(OrderManagement orderManagement, QuoteDataFeed quoteDataFeed);

    // initializes the strategy through a JSON object that inclides all the parameterss
    public iStrategy Initialize(JSONObject parameters);

    // just for convenience
    public ArrayList<String> GetUnderlyings();

    public ArrayList<String> GetAssetClasses();

    public void FetchQuoteData(QuoteDataFeed quoteDataFeed);
}
