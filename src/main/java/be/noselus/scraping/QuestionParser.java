package be.noselus.scraping;

import be.noselus.model.Assembly;
import be.noselus.model.PersonSmall;
import be.noselus.model.Question;
import be.noselus.repository.AssemblyRegistry;
import be.noselus.repository.PoliticianRepository;
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

    private String url = "http://parlement.wallonie.be/content/print_container.php?print=quest_rep_voir.php&type=all&id_doc=";

    private final PoliticianRepository politicianRepository;
    private final Assembly walloonParliament;

    public QuestionParser(final PoliticianRepository politicianRepository, final AssemblyRegistry assemblyRegistry) {
        this.politicianRepository = politicianRepository;
        walloonParliament = assemblyRegistry.findId(1);
    }

    public Question parse(int id) throws IOException {

        DateTimeFormatter dateFormatter = getDateFormatter();

        Question model = new Question();

        model.assembly_ref = id + "";
        model.assembly = walloonParliament;
        Document doc = Jsoup.parse(new URL(url + id).openStream(), "iso-8859-1", url + id);
        List<String> fields;

        // Extract Title
        fields = extract(doc, "h1");

        model.title = fields.get(0);

        // Extract Question & Response
        fields = extract(doc, "h2");

        model.date_asked = LocalDate.parse(fields.get(0).replace("Question écrite du ", "").replace(" ", ""), dateFormatter);
        if (fields.size() > 1) {
            model.date_answered = LocalDate.parse(fields.get(1).replaceFirst("Réponse(.)* du ", "").replace(" ", ""), dateFormatter);
        }

        // Extract From/To
        fields = extract(doc, "li.evid02");

        final String askedByName = fields.get(0).replace("de ", "");
        if (!politicianRepository.getPoliticianByName(askedByName).isEmpty()) {
            model.asked_by = politicianRepository.getPoliticianByName(askedByName).get(0).id;
        } else {
            model.asked_by = 0;
        }

        // Separate title from askedTo field
        String askedTo = fields.get(1).replace("à ", "");
        int pos = askedTo.indexOf(',');
        String name;
        if (pos > 0) {
            name = askedTo.substring(0, pos).trim();
            // String title = askedTo.substring(pos+1)
        } else {
            name = askedTo.trim();
        }

        List<PersonSmall> list = politicianRepository.getPoliticianByName(name);
        if (!list.isEmpty()) {
            model.asked_to = list.get(0).id;
        }

        if (fields.size() > 2) {
            model.answered_by = politicianRepository.getPoliticianByName(fields.get(2).replace("de ", "")).get(0).id;
        }

        // Extract Metadata
        fields = extract(doc, "div#print_container > ul li");

        model.session = fields.get(0).replace("Session : ", "");
        model.year = Integer.parseInt(fields.get(1).replace("Année : ", ""));
        model.number = fields.get(2).replace("N° : ", "");

        // Extract Texts
        fields = extract(doc, "div#print_container div + div");
        model.question_text = fields.get(0);
        if (fields.size() > 2) {
            model.answer_text = fields.get(2);
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
        for (Element e : data) {
            String item = StringEscapeUtils.unescapeHtml(e.html());
            items.add(item);
        }

        return items;
    }
}
