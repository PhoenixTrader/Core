package DataFeed.CalendarFeed;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import org.jsoup.nodes.Document;

public class FetchForexFactoryContent extends FetchRawHTML {

    // ----- Variables
	protected String website;

	// ----- Get functions
	public String GetWebsite() {
		return this.website;
	}

	// ----- Setter
	public void SetWebsite(String website) {
		this.website = website;
	}

    public Hashtable<String, String> monthToString() {
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

	protected Hashtable<String, String> afternoonDictionary() {
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
    // ----- Methods
	public Document FetchForexFactoryHTML(String websiteName){
	FetchRawHTML FetchRawHTML = new FetchRawHTML();
	Document fullHTMLPage = FetchRawHTML.fetchHtmlText(this.website);
	return fullHTMLPage;
	}

	public String GetFullWebsiteNameBasedOnDate(ZonedDateTime dateFromeQuote) {
		// Parameters type are choosen based on the need of the webpage requirement
		Hashtable<String, String> monthToString = monthToString();

		LocalDate date = dateFromeQuote.toLocalDate();
		String dayAsString = Integer.toString(date.getDayOfMonth());
		String monthString = Integer.toString(date.getMonthValue());
		String yearAsString = Integer.toString(date.getYear());
		monthString = monthToString.get(monthString);
		String fullWebsite = this.website + "/calendar?day=" + monthString + dayAsString + "." + yearAsString;
		return fullWebsite;
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
	public String GetCalendarDate(String dayAsString, int monthAsInteger, String yearAsString) {
        String monthAsString = Integer.toString(monthAsInteger);
        if (monthAsString.length() == 1) {
            monthAsString = "0" + monthAsString;
        }
        String calendarDate = dayAsString + "-" + monthAsString + "-" + yearAsString;
        return calendarDate;
        // Date frormatting: "dd-MM-yyyy"
    }

	public ZonedDateTime createZonedDateTime(LocalDate localDate, String time, String zoneID) {
		// Create TimeKey for Hashtable
		LocalDateTime localDateTime = localDate.atTime(Integer.parseInt(time.substring(0, 2)),
				Integer.parseInt(time.substring(3, 5)), Integer.parseInt(time.substring(6, 8))); // Add time information
		// System.out.println(localDateTime);
		ZoneId zoneId = ZoneId.of(zoneID); // Zone information: e.g. "Asia/Kolkata", "America/New_York"
		ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId); // add zone information
		return zonedDateTime;

	}
    
}
