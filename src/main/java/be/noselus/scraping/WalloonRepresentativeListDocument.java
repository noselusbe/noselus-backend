package be.noselus.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WalloonRepresentativeListDocument {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalloonRepresentativeListDocument.class);

    private final Document webPage;

    public WalloonRepresentativeListDocument(final Document webPage) {
        this.webPage = webPage;
    }

    public List<WalloonRepresentativeDocument> getRepresentatives() throws IOException {
        final List<WalloonRepresentativeDocument> result = new ArrayList<>();
        final Elements links = webPage.select(".panel-heading").select("a[href]");
        for (Element link : links) {
            final WalloonRepresentativeDocument representative = getRepresentative(link);
            LOGGER.trace(representative.getName() + "("+ representative.getParty() +") : " +representative.getId());
            result.add(representative);
        }
        return result;
    }

    private WalloonRepresentativeDocument getRepresentative(final Element link) throws IOException {
        final String url = link.attr("abs:href").replace("wallon","wallonie");
        Document doc;
        try (InputStream in = new URL(url).openStream()) {
            doc = Jsoup.parse(in, "utf-8", url);
        }
        return new WalloonRepresentativeDocument(doc);
    }
}
