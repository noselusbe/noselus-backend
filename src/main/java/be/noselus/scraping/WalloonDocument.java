package be.noselus.scraping;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class WalloonDocument {

    public static final String DATE = ".text-pw-date";
    public static final String QUESTION_OR_ANSWER_HEADER = "h5";
    public static final String QUESTION_OR_ANSWER = ".list-group-item";

    private final Document document;
    private List<String> h5Titles;
    private final Element question;
    private final Element answer;

    public WalloonDocument(final Document document) {
        this.document = document;
        final Elements headers = document.select(QUESTION_OR_ANSWER_HEADER);
        h5Titles = new ArrayList<>(headers.size());
        for (Element element : headers) {
            h5Titles.add(StringEscapeUtils.unescapeHtml(element.html()));
        }
        final Elements subElement = document.select(QUESTION_OR_ANSWER);
        question = subElement.get(0);
        if (subElement.size() > 1) {
            answer = subElement.get(1);
        } else {
            answer = null;
        }

    }

    public String geTitle() {
        Elements data = document.select("h4");
        return StringEscapeUtils.unescapeHtml(data.html());
    }

    public String getQuestionType() {
        return h5Titles.get(0);
    }

    public boolean hasAnswer() {
        return answer != null;
    }

    public String getQuestionAskedBy() {
        final Elements lis = question.select("li");
        return StringEscapeUtils.unescapeHtml(lis.get(1).html()).replace("de ", "").replace(" ", " ");
    }

    public String getQuestionAskedTo() {
        final Elements lis = question.select("li");
        String askedTo =  StringEscapeUtils.unescapeHtml(lis.get(2).html()).replace("à ", "").replace(" ", " ");
        // Separate title from askedTo field
        int pos = askedTo.indexOf(',');
        String name;
        if (pos > 0) {
            name = askedTo.substring(0, pos).trim();
        } else {
            name = askedTo.trim();
        }
        return name;
    }

    public String getAnsweredBy() {
        final Elements lis = answer.select("li");
        return StringEscapeUtils.unescapeHtml(lis.get(1).html()).replace("de ", "").replace(" ", " ");
    }

    public LocalDate getDateAsked() {
        final String dateAsString = document.select(QUESTION_OR_ANSWER_HEADER).get(0).select(DATE).get(0).html();
        return LocalDate.parse(dateAsString, getDateFormatter());
    }

    public LocalDate getDateAnswered() {
        final String responseDateString = document.select(QUESTION_OR_ANSWER_HEADER).get(1).select(DATE).get(0).html();
        return LocalDate.parse(responseDateString, getDateFormatter());
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

    public String getQuestionText() {
        return getListGroupElementText(question);
    }

    public String getAnswerText() {
        return getListGroupElementText(answer);
    }

    private String getListGroupElementText(Element element) {
        final Element clone = element.clone();
        clone.select(QUESTION_OR_ANSWER_HEADER).remove();
        final Elements ul = clone.select("ul");
        ul.get(0).remove();
        return StringEscapeUtils.unescapeHtml(clone.html().replaceAll("^(<br />)*", "").replaceAll("(<br/>)*$", ""));

    }
}
