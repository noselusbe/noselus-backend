package be.noselus.scraping;

import be.noselus.model.Person;
import be.noselus.repository.PoliticianRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class WalloonRepresentativesImporter {

    private final PoliticianRepository politicianRepository;

    @Inject
    public WalloonRepresentativesImporter(final PoliticianRepository politicianRepository) {
        this.politicianRepository = politicianRepository;
    }

    public void updateRepresentativeList() throws IOException {
        WalloonRepresentativeListDocument listDocument;
        final String url = "http://www.parlement-wallon.be/pwpages?p=composition_dep";
        try (InputStream in = new URL(url).openStream()) {
            listDocument = parse(in, url);
        }
        List<Person> representatives = listDocument.getRepresentatives();
        for (Person representative : representatives) {
            politicianRepository.upsertPolitician(representative);
        }
    }

    protected WalloonRepresentativeListDocument parse(InputStream in, String url) throws IOException {
        Document doc = Jsoup.parse(in, "utf-8", url);

        return new WalloonRepresentativeListDocument(doc);
    }


}
