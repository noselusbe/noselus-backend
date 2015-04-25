package be.noselus.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.Normalizer;
import java.util.List;

public class WalloonRepresentativesFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalloonRepresentativesFetcher.class);

    public List<WalloonRepresentativeDocument> searchFor(String name) throws IOException {
        WalloonRepresentativeListDocument listDocument;
        String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFD).replace(' ', '+').replace(' ','+').replaceAll("[^\\p{ASCII}]", "");
//        name = name.replace(' ', '+').replaceAll("[^A-Za-z0-9 /+]", "");
        final String url = "http://www.parlement-wallon.be/pwpages?home_dep_search=" + normalizedName +
                "&p=composition_dep";
        LOGGER.debug("Trying to find using url [{}]", url);
        try (InputStream in = new URL(url).openStream()) {
            listDocument = parse(in, url);
        }
        return listDocument.getRepresentatives();
    }

    protected WalloonRepresentativeListDocument parse(InputStream in, String url) throws IOException {
        Document doc = Jsoup.parse(in, "utf-8", url);
        return new WalloonRepresentativeListDocument(doc);
    }


}
