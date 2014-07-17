package be.noselus.scraping;

import be.noselus.model.Assembly;
import be.noselus.model.PersonSmall;
import be.noselus.model.Question;
import be.noselus.repository.AssemblyRegistry;
import be.noselus.repository.PoliticianRepository;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuestionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionParser.class);

    private static final String DEFAULT_URL = "http://www.parlement-wallon.be/content/print.php?print=interp-questions-voir.php&type=all&id_doc=";

    private final PoliticianRepository politicianRepository;
    private final Assembly WALLOON_PARLIAMENT;

    @Inject
    public QuestionParser(final PoliticianRepository politicianRepository, final AssemblyRegistry assemblyRegistry) {
        this.politicianRepository = politicianRepository;
        WALLOON_PARLIAMENT = assemblyRegistry.findId(1);
    }

    public Question parse(int id) throws IOException {
        final String resource = DEFAULT_URL + id;
        try (InputStream in = new URL(resource).openStream()) {
            return parse(in, resource, id);
        }
    }

    protected Question parse(InputStream in, String url, int id) throws IOException {
        LOGGER.trace("Parsing document {}", url);

        Document doc;
        doc = Jsoup.parse(in, "utf-8", url);

        WalloonDocument document = new WalloonDocument(doc);

        Question question = new Question(WALLOON_PARLIAMENT, id + "", document.geTitle());

        final String type = document.getQuestionType();

        if (!type.startsWith("Question écrite")) {
            LOGGER.trace("Document at {} is not a written question but a ", url, type);
            return null;
        }

        question.dateAsked = document.getDateAsked();
        if (document.hasAnswer()) {
            question.dateAnswered = document.getDateAnswered();
        }

        final String askedByName = document.getQuestionAskedBy();
        if (!politicianRepository.getPoliticianByName(askedByName).isEmpty()) {
            question.askedBy = politicianRepository.getPoliticianByName(askedByName).get(0).id;
        } else {
            question.askedBy = 0;
        }

        List<PersonSmall> list = politicianRepository.getPoliticianByName(document.getQuestionAskedTo());
        if (!list.isEmpty()) {
            question.askedTo = list.get(0).id;
        }

        if (document.hasAnswer()) {
            question.answeredBy = politicianRepository.getPoliticianByName(document.getAnsweredBy()).get(0).id;
        }

        // Extract Metadata
        List<String> fields;
        fields = extract(doc, "div#print_container > ul li");

        question.session = fields.get(0).replace("Session : ", "");
        question.year = Integer.parseInt(fields.get(1).replace("Année : ", ""));
        question.number = fields.get(2).replace("N° : ", "");

        question.questionText = document.getQuestionText();
        if (document.hasAnswer()) {
            question.answerText = document.getAnswerText();
        }
        return question;

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
