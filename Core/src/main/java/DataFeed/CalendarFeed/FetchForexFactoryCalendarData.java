package DataFeed.CalendarFeed;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Infrastructure.Quote.Quote;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FetchForexFactoryCalendarData extends FetchForexFactoryContent {
	// Variables
	
	private ZoneId zdtForexFactory = ZoneId.of("GMT-4"); //Checked with ForexFactory-website. Code get´s GMT-4 times
	private Quote Quote; //Clase from Infrastructure

	public void SetQuote(Quote Quote){
		this.Quote = Quote;
	}


	// Create lists
	public List<String> GetEventList() {
		FetchRawHTML FetchRawHTML = new FetchRawHTML();
		Document fullHTMLPage = FetchRawHTML.fetchHtmlText(this.website);
		List<String> eventList = new ArrayList<>();
		Elements events = fullHTMLPage.select("td.calendar__event");
		for (Element event : events) {
			eventList.add(event.text());
		}
		return eventList;
	};

	public List<String> GetCurrenciesList() {
		FetchRawHTML FetchRawHTML = new FetchRawHTML();
		Document fullHTMLPage = FetchRawHTML.fetchHtmlText(this.website);
		List<String> currencyList = new ArrayList<>();
		Elements currencies = fullHTMLPage.select("td.calendar__currency");
		for (Element currency : currencies) {
			currencyList.add(currency.text());
		}
		return currencyList;
	};

	public List<String> GetTimeList() {
		FetchRawHTML FetchRawHTML = new FetchRawHTML();
		Document fullHTMLPage = FetchRawHTML.fetchHtmlText(this.website);
		List<String> timeList = new ArrayList<>();
		Elements times = fullHTMLPage.select("td.calendar__time");
		for (Element time : times) {
			String StringTime = time.text();
			StringTime = ConvertedTimeCalendar(StringTime);
			// System.out.println(StringTime);
			try{
				if (StringTime.isEmpty()) {
					timeList.add(ConvertedTimeCalendar(timeList.get(timeList.size() - 1)));
				} else
					timeList.add(StringTime);
				}
			catch(IndexOutOfBoundsException outOfBounds){
				System.out.println("Error out of bounds");
			}	
		}
		return timeList;
	};

	public Hashtable<String, List<String>> eventsList(ZonedDateTime dateFromeDF) {
		List<String> daysEvents = GetEventList();
		List<String> daysCurrencies = GetCurrenciesList();
		List<String> daysActuals = GetActualList();
		List<String> daysTime = GetTimeList();
		List<String> calendarDateTimes = new ArrayList<>();
		String calendarTime;
		LocalDate date = dateFromeDF.toLocalDate();
		for (int i = 0; i < daysTime.size(); i++) {
			String checker = daysTime.get(i);
			int checkerlength = checker.length();
			if (checkerlength == 8) {
				calendarTime = createcalendarDateTime(date, daysTime.get(i));
				calendarDateTimes.add(calendarTime);
			} else {
				int previousCounter = i - 1;
				calendarTime = createcalendarDateTime(date, daysTime.get(previousCounter));
				calendarDateTimes.add(calendarTime);
			}
		}
		Hashtable<String, List<String>> todaysEvents = new Hashtable<String, List<String>>();
		todaysEvents = todaysEvents(daysEvents.size(), daysEvents, daysCurrencies, calendarDateTimes, daysActuals);
		return todaysEvents;
	}

	public String createcalendarDateTime(LocalDate localDate, String time) {
		// Create TimeKey for Hashtable
		System.out.print(time);
		if (time == "All Day") {
			time = "00:00:00";
		}
		LocalDateTime localDateTime = localDate.atTime(Integer.parseInt(time.substring(0, 2)),
				Integer.parseInt(time.substring(3, 5)), Integer.parseInt(time.substring(6, 8))); // Add time information
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String calendarDateTime = localDateTime.format(formatter);
		return calendarDateTime;
	}


	// Supporting methods time lists
	public List<ZonedDateTime> zdtCalendar(LocalDate localDate, List<String> listCalendarTime) {
		List<ZonedDateTime> calendarDateTimes = new ArrayList<>();
		ZonedDateTime calendarTime;
		for (int i = 0; i < listCalendarTime.size(); i++) {
			String checker = listCalendarTime.get(i);
			int checkerlength = checker.length();
			if (checkerlength == 8) {
				calendarTime = zdtForexCalendar(localDate, listCalendarTime.get(i));
				calendarDateTimes.add(calendarTime);
			} else if (checkerlength == 7) {
				calendarTime = zdtForexCalendar(localDate, "00:00:00");
				calendarDateTimes.add(calendarTime);
			} else {
				int previousCounter = i - 1;
				calendarTime = zdtForexCalendar(localDate, listCalendarTime.get(previousCounter));
				calendarDateTimes.add(calendarTime);
			}
		}
		return calendarDateTimes;
	};

	public ZonedDateTime zdtForexCalendar(LocalDate localDate, String time) {
		if (time == "All Day" || time.isEmpty()) {
			time = "00:00:00";
		}
		LocalDateTime localDateTime = localDate.atTime(Integer.parseInt(time.substring(0, 2)),
				Integer.parseInt(time.substring(3, 5)), Integer.parseInt(time.substring(6, 8)));
		ZonedDateTime zdtconvertedFFtoDF = localDateTime.atZone(zdtForexFactory); // add zone information
		zdtconvertedFFtoDF = zdtFFtoDataFeedTime(zdtconvertedFFtoDF);
		return zdtconvertedFFtoDF;
	};
 
	//Single version: Convert forexfactory times to datafeed times based on zoned ID
	public ZonedDateTime zdtFFtoDataFeedTime(ZonedDateTime zdtForexFactory){
			ZonedDateTime dataFeedTime = zdtForexFactory.withZoneSameInstant(this.Quote.GetTime().getZone());
			return dataFeedTime;
	}

	//List version: Convert forexfactory times to datafeed times based on zoned ID
	public List<ZonedDateTime> zdtDataFeedTime(List<ZonedDateTime> zdtForexFactory){
		List<ZonedDateTime> dataFeedTime = new ArrayList<>();
		for (int i = 0; i < zdtForexFactory.size(); i++){
			ZonedDateTime dataFeedCalendar = zdtForexFactory.get(i).withZoneSameInstant(this.Quote.GetTime().getZone());
			dataFeedTime.add(dataFeedCalendar);

		}
		return dataFeedTime;
	}

	// continue creating lists
	public List<String> GetActualList() {
		FetchRawHTML FetchRawHTML = new FetchRawHTML();
		Document fullHTMLPage = FetchRawHTML.fetchHtmlText(this.website);
		List<String> actualList = new ArrayList<>();
		Elements actuals = fullHTMLPage.select("td.calendar__actual");
		for (Element actual : actuals) {
			actualList.add(actual.text());
		}
		return actualList;
	};

	public List<String> GetImpactList() {
		FetchRawHTML FetchRawHTML = new FetchRawHTML();
		Document fullHTMLPage = FetchRawHTML.fetchHtmlText(this.website);
		List<String> impactList = new ArrayList<>();
		Elements impacts = fullHTMLPage.select("span");
		for (Element impact : impacts) {
			impactList.add(impact.text());
		}
		return impactList;
	};

	public String ConvertedTimeCalendar(String calendarTime) {

		Hashtable<String, String> afternoonHours = this.afternoonDictionary();

		if (calendarTime.indexOf("Day") != -1) {
			return "00:00:01";
		} else if (calendarTime.indexOf("pm") != -1) {
			String cleanedTime = calendarTime.replace("pm", "").strip();
			String[] timeParts = cleanedTime.split(":");
			String hour = timeParts[0];
			String minutes = timeParts[1];
			hour = afternoonHours.get(hour);
			String completeTime = hour + ":" + minutes + ":00";
			return completeTime;
		} else if (calendarTime.indexOf("am") != -1) {
			String cleanedTime = calendarTime.replace("am", "").strip();
			String[] timeParts = cleanedTime.split(":");
			String hour = timeParts[0];
			String minutes = timeParts[1];
			if (hour.length() == 1) {
				hour = "0" + hour;
			}
			String completeTime = hour + ":" + minutes + ":00";
			return completeTime;
		} else {
			return calendarTime;
		}
	};

	// Methods for calendar data
	private List<String> events() {
		return GetEventList();
	};

	private List<String> currencies() {
		return GetCurrenciesList();
	};

	private List<String> actuals() {
		return GetActualList();
	};

	public List<ZonedDateTime> times() {
		List<String> GetDateTimeAsStrings;
		List<ZonedDateTime> GetDateTimeAsZonedDateTime;
		GetDateTimeAsStrings = GetTimeList();
		GetDateTimeAsZonedDateTime = zdtCalendar(this.Quote.GetTime().toLocalDate(), GetDateTimeAsStrings);
		System.out.println("__________________________");
		return GetDateTimeAsZonedDateTime;
	};

	public String eventBasedOnTime(ZonedDateTime lookUpTime){
		int lookUpTimeIndex = times().indexOf(lookUpTime);
		return events().get(lookUpTimeIndex);
	}

	public Map<String, Object> eventBasedOnTimeDict(ZonedDateTime TimePriceFeed){
		int lookUpTimeIndex = times().indexOf(TimePriceFeed);
		Map<String, Object> calendarEvent = new HashMap<String, Object>();
		calendarEvent.put("TimePriceFeed", TimePriceFeed);
		calendarEvent.put("TimeOfEvent", times().get(lookUpTimeIndex));
		calendarEvent.put("Event", events().get(lookUpTimeIndex));
		calendarEvent.put("Currency", currencies().get(lookUpTimeIndex));
		calendarEvent.put("Actual", actuals().get(lookUpTimeIndex));
		return calendarEvent;
	}

	public String currenciesBasedOnTime(ZonedDateTime lookUpTime){
		int lookUpTimeIndex = times().indexOf(lookUpTime);
		return currencies().get(lookUpTimeIndex);
	}

	// Search methods to get values
	public Multimap<String, List<Object>> Searchevent(String ItemToSearch) {
		int position = getIndexCalendarStrings(ItemToSearch);
		String event = events().get(position);
		String currency = currencies().get(position);
		ZonedDateTime time = times().get(position);
		String actual = actuals().get(position);
		ArrayList<Object> eventData = new ArrayList<Object>();
		eventData.add(currency);
		eventData.add(time);
		eventData.add(actual);
		Multimap<String, List<Object>> eventDictionary = ArrayListMultimap.create();
		eventDictionary.put(event, eventData);
		return eventDictionary;
	};

	public Multimap<ZonedDateTime, List<Object>> SearchDateTime(ZonedDateTime ItemToSearch) {
		Multimap<ZonedDateTime, List<Object>> eventDictionary = ArrayListMultimap.create();
		int position = getIndexZonedDateTime(ItemToSearch);
		if (position > -1) {

			String event = events().get(position);
			String currency = currencies().get(position);
			ZonedDateTime time = times().get(position);
			String actual = actuals().get(position);
			ArrayList<Object> timeData = new ArrayList<Object>();
			timeData.add(event);
			timeData.add(currency);
			timeData.add(actual);

			eventDictionary.put(time, timeData);

			return eventDictionary;
		} else {
			// Nothing is returned when no match is found
			return null;
		}
	};

	public Multimap<String, List<Object>> SearchCurrency(String ItemToSearch) {
		int position = getIndexCalendarStrings(ItemToSearch);
		String event = events().get(position);
		String currency = currencies().get(position);
		ZonedDateTime time = times().get(position);
		String actual = actuals().get(position);
		ArrayList<Object> eventData = new ArrayList<Object>();
		eventData.add(event);
		eventData.add(time);
		eventData.add(actual);
		Multimap<String, List<Object>> eventDictionary = ArrayListMultimap.create();
		eventDictionary.put(currency, eventData);

		return eventDictionary;
	};

	public Multimap<String, List<Object>> SearchActual(String ItemToSearch) {
		int position = getIndexCalendarStrings(ItemToSearch);
		String event = events().get(position);
		String currency = currencies().get(position);
		ZonedDateTime time = times().get(position);
		String actual = actuals().get(position);
		ArrayList<Object> eventData = new ArrayList<Object>();
		eventData.add(event);
		eventData.add(currency);
		eventData.add(time);
		Multimap<String, List<Object>> eventDictionary = ArrayListMultimap.create();
		eventDictionary.put(actual, eventData);
		return eventDictionary;
	};


	// Methods supporting search
	public int getIndexCalendarStrings(String ItemToSearch) {
		int ItemPosition = events().indexOf(ItemToSearch);
		return ItemPosition;
	};

	public int getIndexZonedDateTime(ZonedDateTime itemToSearch) {
		int ItemPosition = times().indexOf(itemToSearch);
		return ItemPosition;
	};

	// Methods directly used in Strategy
	public List<String> daysEvents = null;
	public List<String> daysCurrencies = null;
	public List<String> daysActuals = null;
	public List<String> daysTime = null;
	public Hashtable<String, List<String>> todaysEvents = new Hashtable<String, List<String>>();
	
	public void lCalDataOLD(ZonedDateTime date){
		daysEvents = GetEventList();
		daysCurrencies = GetCurrenciesList();
		daysActuals = GetActualList();
		daysTime = GetTimeList();
		todaysEvents = todaysEvents(daysEvents.size(), daysEvents, daysCurrencies,
				daysTime, daysActuals);
	}

	public void lCalData(){
		daysEvents = GetEventList();
		daysCurrencies = GetCurrenciesList();
		daysActuals = GetActualList();
		daysTime = GetTimeList();
		todaysEvents = todaysEvents(daysEvents.size(), daysEvents, daysCurrencies,
				daysTime, daysActuals);
	}

	public Hashtable<String, List<String>> todaysEvents(Integer counter, List<String> events, List<String> currencies,
			List<String> times, List<String> actuals) {
		Hashtable<String, List<String>> eventDictionary = new Hashtable<String, List<String>>();
		for (int i = 0; i < counter; i++) {
			List<String> eventDataForDictionary = eventData(i, times, currencies, actuals);
			eventDictionary.put(events.get(i), eventDataForDictionary);
		}
		return eventDictionary;
	}

	public List<String> eventData(Integer counter, List<String> events, List<String> currencies, List<String> actuals) {
		ArrayList<String> eventData = new ArrayList<String>();
		eventData.add(currencies.get(counter));
		eventData.add(events.get(counter));
		eventData.add(actuals.get(counter));
		return eventData;
	}

	
}
