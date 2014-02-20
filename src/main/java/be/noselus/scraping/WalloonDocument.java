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
    private List<String> h2Titles;

    public WalloonDocument(final Document document) {
        this.document = document;
        final Elements h2 = document.select("h2");
        h2Titles = new ArrayList<>(h2.size());
        for (Element element : h2) {
            h2Titles.add(StringEscapeUtils.unescapeHtml(element.html()));
        }
    }

    public String geTitle() {
        Elements data = document.select("h1");
        return StringEscapeUtils.unescapeHtml(data.html());
    }

    public boolean hasAnswer(){
        return h2Titles.size() == 2;
    }

    public LocalDate getDateAsked(){
        return LocalDate.parse(h2Titles.get(0).replace("Question écrite du ", "").replace(" ", ""), getDateFormatter());
    }

    public LocalDate getDateAnswered(){
        return LocalDate.parse(h2Titles.get(1).replaceFirst("Réponse(.)* du ", "").replace(" ", ""), getDateFormatter());
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
}
