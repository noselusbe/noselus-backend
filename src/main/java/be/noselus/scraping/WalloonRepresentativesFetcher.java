package be.noselus.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class WalloonRepresentativesFetcher {

    public List<WalloonRepresentativeDocument> searchFor(String name) throws IOException {
        WalloonRepresentativeListDocument listDocument;

        final String url = "http://www.parlement-wallon.be/pwpages?home_dep_search=" + name.replace(' ', '+') +
                "&p=composition_dep";
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
