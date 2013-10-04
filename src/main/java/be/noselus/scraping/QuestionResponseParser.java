package be.noselus.scraping;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import be.noselus.model.QuestionResponse;

public class QuestionResponseParser {

	public static QuestionResponse parse(String url) throws IOException {
//		String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&id_doc=36256&type=all";
		
		QuestionResponse model = new QuestionResponse();
		
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-1", url);
        List<String> fields;
		
        // Extract Title
        fields = extract(doc, "h1");
        
        model.title = fields.get(0);
		
		// Extract Question & Response
		fields = extract(doc, "h2");
		
		model.question = fields.get(0);
		model.response = fields.get(1);
        
		// Extract From/To
		fields = extract(doc, "li.evid02");
        
		model.question_from = fields.get(0);
		model.question_to = fields.get(1);
		model.response_from = fields.get(2);
		
        // Extract Metadata
        fields = extract(doc, "div#print_container > ul li");
        
		model.session = fields.get(0);
		model.year = fields.get(1);
		model.number = fields.get(2);
        
        return model;
	}

	private static List<String> extract(Document doc, String tag) {
		Elements data = doc.select(tag);
        
		List<String> items = new ArrayList<>();
        for (Element e: data) {
        	String item = StringEscapeUtils.unescapeHtml(e.html());
        	items.add(item);
        	System.out.println(item);
        }
        
        return items;
	}
}
