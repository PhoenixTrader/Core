// package Strategies.EventTrigger;

// import java.time.ZonedDateTime;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import DataFeed.QuoteDataFeed.QuoteDataFeed;
// import Infrastructure.Order.Order;
// import Infrastructure.OrderManagement.OrderManagement;
// import Infrastructure.Quote.Quote;
// import Strategies.AlphaEnginePlus.ExposureManagement;

// public class EventTrading {
    
// 	   //--- print management
//        private String            file;
//        private int               file_handle;
//           //--- measure definer
//        private String            measure;
//           //-- the symbol to be traded
//        private String            underlying;

//           //--Event(s)
        
//        private String event;

//        private ArrayList<String> events;

//        private ArrayList<String> assetClasses;

//     /**
//      * @return String return the file
//      */
//     public String getFile() {
//         return file;
//     }

//     /**
//      * @param file the file to set
//      */
//     public void setFile(String file) {
//         this.file = file;
//     }

//     /**
//      * @return int return the file_handle
//      */
//     public int getFile_handle() {
//         return file_handle;
//     }

//     /**
//      * @param file_handle the file_handle to set
//      */
//     public void setFile_handle(int file_handle) {
//         this.file_handle = file_handle;
//     }

//     /**
//      * @return String return the measure
//      */
//     public String getMeasure() {
//         return measure;
//     }

//     /**
//      * @param measure the measure to set
//      */
//     public void setMeasure(String measure) {
//         this.measure = measure;
//     }

//     /**
//      * @return String return the underlying
//      */
//     public String getUnderlying() {
//         return underlying;
//     }

//     /**
//      * @param underlying the underlying to set
//      */
//     public void setUnderlying(String underlying) {
//         this.underlying = underlying;
//     }

//     /**
//      * @return String return the event
//      */
//     public String getEvent() {
//         return event;
//     }

//     /**
//      * @param event the event to set
//      */
//     public void setEvent(String event) {
//         this.event = event;
//     }

//     /**
//      * @return ArrayList<String> return the events
//      */
//     public ArrayList<String> getEvents() {
//         return events;
//     }

//     /**
//      * @param events the events to set
//      */
//     public void setEvents(ArrayList<String> events) {
//         this.events = events;
//     }

//     /**
//      * @return ArrayList<String> return the assetClasses
//      */
//     public ArrayList<String> getAssetClasses() {
//         return assetClasses;
//     }

//     /**
//      * @param assetClasses the assetClasses to set
//      */
//     public void setAssetClasses(ArrayList<String> assetClasses) {
//         this.assetClasses = assetClasses;
//     }

//     private ForexFactoryCalendarData FFCD = new ForexFactoryCalendarData();;

// 	//+------------------------------------------------------------------+
// 	//|   Core trading function, based on seen event(s) from calendar    |
// 	//|                                                                  |
// 	//+------------------------------------------------------------------+

//     public Map<String, ArrayList<Order>> Trade(OrderManagement orderManagement, QuoteDataFeed quoteDataFeed, Quote quote, ArrayList<String> eventList){
    
        
//         this.FFCD.SetWebsite("https://www.forexfactory.com");
//         //--- trading execution
//         Map<String, ArrayList<Order>> orders = new HashMap<String, ArrayList<Order>>();
//         orders.put(this.underlying, new ArrayList<Order>());
        
//         ArrayList<Order> Eventorders = new ArrayList<Order>();

//         FFCD.SetQuote(quote);
//         String sCurrentCalWebsite = FFCD.GetFullWebsiteNameBasedOnDate(quote.GetTime());    
        
//         // Start Part 3/3 Calendar - Look for time match with datafeed
//         String sNewCalWebsite = FFCD.GetFullWebsiteNameBasedOnDate(quote.GetTime());

//         if (sCurrentCalWebsite.equals(sNewCalWebsite) != true || FFCD.fullHTMLPage == null) {
//             FFCD.SetQuote(quote);
//             FFCD.lCalData();
//             sCurrentCalWebsite = FFCD.GetFullWebsiteNameBasedOnDate(quote.GetTime());
//         } else {
//             System.out.println("Did not fetch calendar");
//         }
//         List<ZonedDateTime> zonedDateTimesCalendar = FFCD
//                 .zdtCalendar(quote.GetTime().toLocalDate(), FFCD.GetTimeList()); //"Europe/Berlin"                 
        
//         if (zonedDateTimesCalendar.contains(quote.GetTime())) {

//             //orders.put(this.underlying, new ArrayList<Order>());
//             int identifier = 1;
//             Order order = new Order( "OPEN", "BUY", 100, "FX", "BTCUSD", 
//                  quoteDataFeed, 1, identifier, "comment");

//             //ordersFromStrategy = EventTrading.Trade(orderManagement, quoteDataFeed);
//             //orderManagement.FillAndFetch("FX", ordersFromStrategy);
//             //accountManagement.FetchData(orderManagement, quoteDataFeed);

//             Eventorders.add(order);
//             orders.put(this.underlying, Eventorders);

//         } else {
//             System.out.println("Not in ---- " + quote.GetTime());
//             System.out.println("Not in ---- " + FFCD.GetEventList());
//             System.out.println("Not in ---- " + FFCD.GetTimeList());   //daysTime
//         }
//         // End Part 3/3 Calendar - Look for time match with datafeed

//         //--- trade execution
//         orders.get(this.underlying);

//         System.out.println(orders);
//         return orders;
//         }



// }