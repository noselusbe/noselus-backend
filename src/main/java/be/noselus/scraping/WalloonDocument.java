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

    private final Document document;
    private List<String> h5Titles;
    private final Element question;
    private final Element answer;

    public WalloonDocument(final Document document) {
        this.document = document;
        final Elements h5 = document.select("h5");
        h5Titles = new ArrayList<>(h5.size());
        for (Element element : h5) {
            h5Titles.add(StringEscapeUtils.unescapeHtml(element.html()));
        }
        //final Elements subElement = document.select("div#print_container ul");
        final Elements subElement = document.select(".list-group-item");
        question = subElement.get(0);
        if (subElement.size() > 1){
            answer = subElement.get(1);
        } else {
            answer = null;
        }

    }

    public String geTitle() {
        Elements data = document.select("h4");
        return StringEscapeUtils.unescapeHtml(data.html());
    }

    public String getQuestionType(){
        return h5Titles.get(0);
    }

    public boolean hasAnswer(){
        return h5Titles.size() == 2;
    }

    public String getQuestionAskedBy(){
        final Elements lis = question.select("li");
        return StringEscapeUtils.unescapeHtml(lis.get(1).html()).replace("de ", "").replace(" ", " ");
    }

    public String getQuestionAskedTo(){
        final Elements lis = question.select("li");
        return StringEscapeUtils.unescapeHtml(lis.get(2).html()).replace("à ", "").replace(" ", " ");
    }

    public String getAnsweredBy(){
        final Elements lis = answer.select("li");
        return StringEscapeUtils.unescapeHtml(lis.get(1).html()).replace("de ", "").replace(" ", " ");
    }

    public LocalDate getDateAsked(){
        final String dateAsString = document.select("h5").get(0).select(".text-pw-date").get(0).html();
        return LocalDate.parse(dateAsString, getDateFormatter());
    }

    public LocalDate getDateAnswered(){
        final String responseDateString = document.select("h5").get(1).select(".text-pw-date").get(0).html();
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

    private String getListGroupElementText(Element element){
        final Element clone = element.clone();
        clone.select("h5").remove();
        final Elements ul = clone.select("ul");
        ul.get(0).remove();
        return  StringEscapeUtils.unescapeHtml(clone.html().replaceAll("^(<br />)*", "").replaceAll("(<br/>)*$", ""));

    }
}
