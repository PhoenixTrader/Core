package DataFeed.CalendarFeed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import javax.script.ScriptException;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import DataFeed.WebScraper.CHttpRequester;

public class GetForexFactoryCalendarPage {

    // ----- Variables

    private String website;

    public Hashtable<String, String> monthToString() {
        // creating a My HashTable Dictionary
        Hashtable<String, String> monthConverter = new Hashtable<String, String>();
        monthConverter.put("1", "jan");
        monthConverter.put("2", "feb");
        monthConverter.put("3", "mar");
        monthConverter.put("4", "apr");
        monthConverter.put("5", "may");
        monthConverter.put("6", "jun");
        monthConverter.put("7", "jul");
        monthConverter.put("8", "aug");
        monthConverter.put("9", "sep");
        monthConverter.put("10", "oct");
        monthConverter.put("11", "nov");
        monthConverter.put("12", "dec");
        return monthConverter;
    }

    // ----- Get functions
    public String GetWebsite(String website) {
        return this.website;
    }

    // ----- Setter
    public void SetWebsite(String website) {
        this.website = website;
    }

    // ----- Methods
    public String GetCalendarDate(String dayAsString, int monthAsInteger, String yearAsString) {
        String monthAsString = Integer.toString(monthAsInteger);
        if (monthAsString.length() == 1) {
            monthAsString = "0" + monthAsString;
        }
        String calendarDate = dayAsString + "-" + monthAsString + "-" + yearAsString;
        return calendarDate;
        // "dd-MM-yyyy"
    }

	public String GetFullWebsiteNameBasedOnDate(ZonedDateTime dateFromeDF, String website) {
		// Parameters type are choosen based on the need of the webpage requirement
		Hashtable<String, String> monthToString = monthToString();

		LocalDate date = dateFromeDF.toLocalDate();
		String dayAsString = Integer.toString(date.getDayOfMonth());
		String monthString = Integer.toString(date.getMonthValue());
		String yearAsString = Integer.toString(date.getYear());
		monthString = monthToString.get(monthString);
		String fullWebsite = website + "/calendar?day=" + monthString + dayAsString + "." + yearAsString;
		return fullWebsite;
	}

	public Document GetCalendarWebsite(String website) {

		CHttpRequester requester = new CHttpRequester();
		Document webSiteContent = null;
		try {
			webSiteContent = requester.get(website);
			// htmlPage = webSiteContent.select("tr");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return webSiteContent;
	}

	public List<String> GetEventList(Document fullHTMLPage) {

		List<String> eventList = new ArrayList<>();
		Elements events = fullHTMLPage.select("td.calendar__event");
		for (Element event : events) {
			eventList.add(event.text());
		}
		;
		return eventList;
	};

	public List<String> GetCurrenciesList(Document websiteData) {

		List<String> currencyList = new ArrayList<>();
		Elements currencies = websiteData.select("td.calendar__currency");
		for (Element currency : currencies) {
			currencyList.add(currency.text());
		}
		return currencyList;
	}

	public List<String> GetTimeList(Document websiteData) {

		List<String> timeList = new ArrayList<>();
		Elements times = websiteData.select("td.calendar__time");
		for (Element time : times) {
			String StringTime = time.text();
			StringTime = ConvertedTimeCalendar(StringTime);
			timeList.add(StringTime);
		}
		return timeList;
	};

	public List<String> GetActualList(Document websiteData) {

		List<String> actualList = new ArrayList<>();
		Elements actuals = websiteData.select("td.calendar__actual");
		for (Element actual : actuals) {
			actualList.add(actual.text());
		}
		;
		return actualList;
	};

	public List<String> GetImpactList(Document websiteData) {

		List<String> impactList = new ArrayList<>();
		Elements impacts = websiteData.select("span");
		for (Element impact : impacts) {
			impactList.add(impact.text());
		}
		;
		return impactList;
	}

    public String ConvertedTimeCalendar(String calendarTime) {

		Hashtable<String, String> afternoonHours = afternoonDictionary();

		if (calendarTime == "Day") {
			return "00:00:00";
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
	}

    public Hashtable<String, String> afternoonDictionary() {
		// creating a My HashTable Dictionary
		Hashtable<String, String> afternoonHours = new Hashtable<String, String>();
		afternoonHours.put("1", "13");
		afternoonHours.put("2", "14");
		afternoonHours.put("3", "15");
		afternoonHours.put("4", "16");
		afternoonHours.put("5", "17");
		afternoonHours.put("6", "18");
		afternoonHours.put("7", "19");
		afternoonHours.put("8", "20");
		afternoonHours.put("9", "21");
		afternoonHours.put("10", "22");
		afternoonHours.put("11", "23");
		afternoonHours.put("12", "00");
		return afternoonHours;
	}

    
}
