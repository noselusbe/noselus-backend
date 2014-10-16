package be.noselus.scraping;

import be.noselus.model.Person;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class WalloonRepresentativeListDocument {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalloonRepresentativeListDocument.class);

    private final Document webPage;

    public WalloonRepresentativeListDocument(final Document webPage) {
        this.webPage = webPage;
    }


    public List<Person> getRepresentatives() throws IOException {
        final Elements links = webPage.select(".panel-heading").select("a[href]");
        for (Element link : links) {
            final WalloonRepresentativeDocument representative = getRepresentative(link);
            LOGGER.trace(representative.getName() + "("+ representative.getParty() +") : " +representative.getId());
        }
        return Collections.emptyList();
    }

    private WalloonRepresentativeDocument getRepresentative(final Element link) throws IOException {
        final String url = link.attr("abs:href");
        Document doc;
        try (InputStream in = new URL(url).openStream()) {
            doc = Jsoup.parse(in, "utf-8", url);
        }
        return new WalloonRepresentativeDocument(doc);
    }
}
