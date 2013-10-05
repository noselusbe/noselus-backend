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
		QuestionResponse model = new QuestionResponse();
		
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-1", url);
        List<String> fields;
		
        // Extract Title
        fields = extract(doc, "h1");
        
        model.title = fields.get(0);
		
		// Extract Question & Response
		fields = extract(doc, "h2");
		
		model.question_date = fields.get(0).replace("Question écrite du ", "");
		model.response_date = fields.get(1).replace("Réponse du ", "");
        
		// Extract From/To
		fields = extract(doc, "li.evid02");
        
		model.question_from = fields.get(0).replace("de ", "");
		model.question_to = fields.get(1).replace("à ", "");
		model.response_from = fields.get(2).replace("de ", "");
		
        // Extract Metadata
        fields = extract(doc, "div#print_container > ul li");
        
		model.session = fields.get(0).replace("Session : ", "");
		model.year = fields.get(1).replace("Année : ", "");
		model.number = fields.get(2).replace("N° : ", "");
        
		// Extract Texts
		fields = extract(doc, "div#print_container div + div");
		model.questionText = fields.get(0);
		model.responseText = fields.get(2);
		
        return model;
	}

	protected static List<String> extract(Document doc, String tag) {
		Elements data = doc.select(tag);
        
		List<String> items = new ArrayList<>();
        for (Element e: data) {
        	String item = StringEscapeUtils.unescapeHtml(e.html());
        	items.add(item);
        }
        
        return items;
	}
}
