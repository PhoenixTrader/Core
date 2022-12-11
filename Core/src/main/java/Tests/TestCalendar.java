package Tests;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.List;
import org.jsoup.nodes.Document;
import DataFeed.CalendarFeed.FetchForexFactoryCalendarData;
import DataFeed.CalendarFeed.FetchForexFactoryContent;

public class TestCalendar {

	public void eventsList() {
		FetchForexFactoryCalendarData calendarfeed = new FetchForexFactoryCalendarData();
        calendarfeed.SetWebsite("https://www.forexfactory.com");
        String calDateTest = calendarfeed.GetCalendarDate("04", 1, "2021");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(calDateTest, formatter);
        String TESTTIME = "9:00am";
        TESTTIME = calendarfeed.ConvertedTimeCalendar(TESTTIME);
        ZonedDateTime tZone = calendarfeed.createZonedDateTime(localDate, TESTTIME, "Asia/Kolkata");
        String generate_websiteName = calendarfeed.GetFullWebsiteNameBasedOnDate(tZone);        
        System.out.println(generate_websiteName);
        calendarfeed.SetWebsite(generate_websiteName);
        System.out.println(calendarfeed.GetWebsite());
        Hashtable<String, List<String>> todaysEvents = calendarfeed.eventsList(tZone);
        System.out.println(todaysEvents);
	}

	public void test_generate_websiteName()
	{
	
    FetchForexFactoryContent calendarfeed = new FetchForexFactoryContent();
    calendarfeed.SetWebsite("https://www.forexfactory.com");
	
    String TESTTIME = "9:00am";
    TESTTIME = calendarfeed.ConvertedTimeCalendar(TESTTIME);
	String calDateTest = calendarfeed.GetCalendarDate("22", 3, "2021");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate localDate = LocalDate.parse(calDateTest, formatter);
    
    ZonedDateTime tZone = calendarfeed.createZonedDateTime(localDate, TESTTIME, "Asia/Kolkata");
    String generate_websiteName = calendarfeed.GetFullWebsiteNameBasedOnDate(tZone);
    System.out.println(generate_websiteName);
	}

    public void test_fetch_page_html(){
		
        //Step 0: Instantiate Object. Set website in main script 
        FetchForexFactoryContent calendarfeed = new FetchForexFactoryContent();
        calendarfeed.SetWebsite("https://www.forexfactory.com");
        
        //Step 1: Get time from Quote => convert to that webpage Object can get correct calendar
        String TESTTIME = "9:00am";
        TESTTIME = calendarfeed.ConvertedTimeCalendar(TESTTIME);
        String calDateTest = calendarfeed.GetCalendarDate("22", 3, "2021");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(calDateTest, formatter);
        
        //Step 2: Set Zone of Date
        ZonedDateTime tZone = calendarfeed.createZonedDateTime(localDate, TESTTIME, "Asia/Kolkata");
        
        //Step 3: Generate Websitename to scrap. Get HTML-structure that will be parsed
        String generate_websiteName = calendarfeed.GetFullWebsiteNameBasedOnDate(tZone);        
        Document fullHTMLPage = calendarfeed.FetchForexFactoryHTML(generate_websiteName);
		System.out.print(fullHTMLPage);
	}


    // public void testPureWebsite(){
		
	// 	LocalDateTime now = LocalDateTime.now();
	// 	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	// 	String today = dtf.format(now);
		
	// 	GetForexFactoryCalendarPage GetForexFactoryCalendarPage = new GetForexFactoryCalendarPage();
	// 	GetForexFactoryCalendarPage.SetWebsite("https://www.forexfactory.com");
    //     String calDateTest = GetForexFactoryCalendarPage.GetCalendarDate("22", 3, "2021");
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    //     LocalDate localDate = LocalDate.parse(calDateTest, formatter);
		
        
    //     Document fullHTMLPage = GetForexFactoryCalendarPage.GetCalendarWebsite(generate_websiteName);
	// 	System.out.print(fullHTMLPage);
	// }

// 	public void zonedDateTimeTest(String zoneID)
// 	{
	
// 	String TESTTIME = "09:00:00";
// 	GetCalendar calendarfeed = new GetCalendar();
// 	TESTTIME = calendarfeed.ConvertedTimeCalendar(TESTTIME);
// 	String calDateTest = calendarfeed.GetCalendarDate("22", 3, "2021");
// 	//System.out.println(TESTTIME);

// 	System.out.println(calDateTest);
//     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//     LocalDate localDate = LocalDate.parse(calDateTest, formatter);
//     System.out.println("localDate: " + localDate);
    
// 	ZonedDateTime testZonedTime = calendarfeed.createZonedDateTime(localDate, TESTTIME, zoneID); 
// 	ZonedDateTime testZonedTime2 = calendarfeed.createZonedDateTime(localDate, TESTTIME, "Asia/Kolkata");
//     System.out.println(testZonedTime);
//     System.out.println(testZonedTime2);
// 	}
	
// 	public void eventsunpacked() {
// 		GetCalendar calendarfeed = new GetCalendar();
//         calendarfeed.SetWebsite("https://www.forexfactory.com");
//         String calDateTest = calendarfeed.GetCalendarDate("22", 3, "2021");
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//         LocalDate localDate = LocalDate.parse(calDateTest, formatter);
// 		String todayCalendar = calendarfeed.GetFullWebsiteNameBasedOnDate(localDate);
// 		Document fullHTMLPage = calendarfeed.GetCalendarWebsite(todayCalendar);
// 		List<String> daysEvents = calendarfeed.GetEventList(fullHTMLPage);
// 		List<String> daysCurrencies = calendarfeed.GetCurrenciesList(fullHTMLPage); 
// 		List<String> daysActuals = calendarfeed.GetActualList(fullHTMLPage);
// 		List<String> daysTime = calendarfeed.GetTimeList(fullHTMLPage);
	     
// 		Hashtable<String, List<String>> todaysEvents = new Hashtable<String, List<String>>();
// 	    todaysEvents = calendarfeed.todaysEvents(daysEvents.size(), daysEvents, daysCurrencies, daysTime, daysActuals);
// 	    System.out.println(todaysEvents);
// 	}
		
// 	public void multiKeys() {
// 		GetCalendar calendarfeed = new GetCalendar();
//         calendarfeed.SetWebsite("https://www.forexfactory.com");
//         String calDateTest = calendarfeed.GetCalendarDate("11", 1, "2021");
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//         LocalDate localDate = LocalDate.parse(calDateTest, formatter);
//         Multimap<String, List<String>> todaysEvents = calendarfeed.eventsListMulti(localDate);
//         //System.out.println(todaysEvents);
//         System.out.println(todaysEvents.get("22-03-2021 22:00:00"));
// 	}
	
// 	public void createCSV() {
// 		GetCalendar calendarfeed = new GetCalendar();
//         calendarfeed.SetWebsite("https://www.forexfactory.com");
//         String calDateTest = calendarfeed.GetCalendarDate("22", 3, "2021");
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//         LocalDate localDate = LocalDate.parse(calDateTest, formatter);
// 		calendarfeed.createCSV(localDate);
// 	}
	
	
// 	public void testCalendarSearcher() {
// 		GetCalendar calendarfeed = new GetCalendar();
// 		String calDateTest = calendarfeed.GetCalendarDate("04", 12, "2020");
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//         LocalDate localDate = LocalDate.parse(calDateTest, formatter);
// 		GetForexFactoryCalendarPage GFCP = new GetForexFactoryCalendarPage();
// 		GFCP.SetWebsite("https://www.forexfactory.com");
// 		Document htmlPage = GFCP.GetCalendarWebsite(localDate);
// 		ForexFactoryCalendarData FFCD = new ForexFactoryCalendarData();
// 		FFCD.SetfullHTMLPage(htmlPage);
// 		FFCD.SetzonedDate(localDate);
// 		List<String> Testevents = FFCD.GetEventList();
// 		System.out.println(Testevents);
		
// 		//Create CalendarData
// 		//ForexFactoryCalendarData newCalendarData = new ForexFactoryCalendarData();
// 		//newCalendarData.SetEvents(daysEvents);
// 		//newCalendarData.SetCurrencies(daysCurrencies);
// 		//newCalendarData.SetTimes(daysTime);
// 		//newCalendarData.SetActuals(daysActuals);
// 		//System.out.println(FFCD.Searchevent("Credit Card Spending y/y"));
// 	}
	
	

	
	
// 	public void doubleClassCalendar(){
		
// 		ForexFactoryCalendarData FFCEV = new ForexFactoryCalendarData();
// 		FFCEV.SetWebsite("https://www.forexfactory.com");
// 		String calDateTest = FFCEV.GetCalendarDate("22", 3, "2021");
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//         LocalDate localDate = LocalDate.parse(calDateTest, formatter);
// 		Document fullHTMLPage = FFCEV.GetCalendarWebsite(localDate);
// 		FFCEV.SetfullHTMLPage(fullHTMLPage);
// 		FFCEV.SetzonedDate(localDate);
// 		ZonedDateTime  time = ZonedDateTime.parse("2021-03-22T07:00-04:00[America/New_York]");
// 		Multimap<ZonedDateTime, List<Object>> SearchDateTime = FFCEV.SearchDateTime(time);
// 		try {
// 			List<Object> singleListe = Iterables.get(SearchDateTime.get(time), 0);
// 			boolean ans = singleListe.get(0) != null;
// 			if (ans == true) {
// 			}
// 			else {
// 				System.out.print("Is empty");
// 			}
// 		}
// 		catch(Exception e) {
// 			System.out.print("Is empty");
// 		}
// 	}
	
// 	public void downloadTimeSpan() {
		
// 		GetCalendarTimeSpan downloadCalendar = new GetCalendarTimeSpan();
// 		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
// 		String startDateTest = "03-01-2021";
// 		String endDateTest = "05-01-2021";
// 		LocalDate startDate = LocalDate.parse(startDateTest, formatter);;
// 		LocalDate endDate = LocalDate.parse(endDateTest, formatter);;
// 		downloadCalendar.SetStartTime(startDate);
// 		downloadCalendar.SetEndTime(endDate);
// 		downloadCalendar.getFullCalendar();
		
// 	}
	
}
