import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class URLGatherer {
	
	public void removeDuplicates(ArrayList<String> input){
		Set<String> hs = new HashSet<>();
		hs.addAll(input);
		input.clear();
		input.addAll(hs);
	}

	public ArrayList<String> gather(String response, int limit) throws IOException {
		ArrayList<String> urls = new ArrayList<String>();
		
		//first connect to website
		Document d=Jsoup.connect("https://www.congress.gov/resources/display/content/Most-Viewed+Bills"
				).timeout(10000).get(); //connect to website
		
		//then parse and gather urls
		Element content = d.getElementById("content");
		Elements links = content.getElementsByClass("external-link");
		for (Element link : links) {
			if (limit>0&&link.attr("href").indexOf("bill")>=0) {
				//if above the limit and is a bill site
			  urls.add(link.attr("href"));
			}
			limit--;
		}

		
		//then remove duplicates
		removeDuplicates(urls);
		
		
		
		//then return urls
		return urls;
	}

}
