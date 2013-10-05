package be.noselus.scraping;

import be.noselus.model.PersonSmall;
import be.noselus.model.Question;
import be.noselus.repository.DeputyRepository;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuestionParser {

    private DeputyRepository deputyRepository;

    public QuestionParser(final DeputyRepository deputyRepository) {
        this.deputyRepository = deputyRepository;
    }

    public Question parse(String url) throws IOException {
		
		DateTimeFormatter dateFormatter = getDateFormatter();
		
		Question model = new Question();
		
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-1", url);
        List<String> fields;
		
        // Extract Title
        fields = extract(doc, "h1");
        
        model.title = fields.get(0);
		
		// Extract Question & Response
		fields = extract(doc, "h2");
		
		// TODO remove after debug
		System.out.println(fields.get(0));
		
		model.date_asked = LocalDate.parse(fields.get(0).replace("Question écrite du ", "").replace(" ", ""), dateFormatter);
		if (fields.size() > 1) {
			model.date_answered = LocalDate.parse(fields.get(1).replaceFirst("Réponse(.)* du ", "").replace(" ", ""), dateFormatter);
		}
        
		// Extract From/To
		fields = extract(doc, "li.evid02");

        final String askedByName = fields.get(0).replace("de ", "");
        if (deputyRepository.getDeputyByName(askedByName).size() > 0) {
        	model.asked_by = deputyRepository.getDeputyByName(askedByName).get(0);
        } else {
        	model.asked_by = new PersonSmall(askedByName, 0);
        }

		// Separate title from askedTo field
		String askedTo = fields.get(1).replace("à ", "");
		int pos = askedTo.indexOf(',');
		if (pos > 0) {
			model.asked_to = new PersonSmall(askedTo.substring(0, pos));
			// String title = askedTo.substring(pos+1)
		} else {
			model.asked_to = new PersonSmall(askedTo);
		}
		
		if (fields.size() > 2) {
			model.answered_by = new PersonSmall(fields.get(2).replace("de ", ""));
		}
		
        // Extract Metadata
        fields = extract(doc, "div#print_container > ul li");
        
		model.session = fields.get(0).replace("Session : ", "");
		model.year = Integer.parseInt(fields.get(1).replace("Année : ", ""));
		model.number = fields.get(2).replace("N° : ", "");
        
		// Extract Texts
		fields = extract(doc, "div#print_container div + div");
		model.questionText = fields.get(0);
		if (fields.size() > 2) {
			model.answerText = fields.get(2);
		}
		
        return model;
	}

	private DateTimeFormatter getDateFormatter() {
		return new DateTimeFormatterBuilder()
			.appendDayOfMonth(2)
			.appendLiteral('/')
			.appendMonthOfYear(2)
			.appendLiteral('/')
			.appendYear(4, 4)
			.toFormatter();
	}

	protected List<String> extract(Document doc, String tag) {
		Elements data = doc.select(tag);
        
		List<String> items = new ArrayList<>();
        for (Element e: data) {
        	String item = StringEscapeUtils.unescapeHtml(e.html());
        	items.add(item);
        }
        
        return items;
	}
}
