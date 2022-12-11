package DataFeed.CalendarFeed;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.script.ScriptException;
import org.jsoup.nodes.Document;
import DataFeed.WebScraper.CHttpRequester;

public class FetchRawHTML {
	// ----- Methods
	public Document webSiteContent(String website) {

		CHttpRequester requester = new CHttpRequester();
		Document webSiteContent = null;
		try {
			webSiteContent = requester.get(website);
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

	public Document fetchHtmlText(String website) {
		return this.webSiteContent(website);
	}

}
