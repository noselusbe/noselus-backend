package be.noselus.scraping;

import be.noselus.model.AssemblyEnum;
import be.noselus.model.PersonFunction;
import be.noselus.repository.PoliticianRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class WalloonRepresentativesCsvImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalloonRepresentativesCsvImporter.class);
    private static final String WALLOON_PARLIAMENT_COMPO_CSV_URL = "http://www.parlement-wallon.be/composition-dep-csv";

    private final PoliticianRepository politicianRepository;

    @Inject
    public WalloonRepresentativesCsvImporter(final PoliticianRepository politicianRepository) {
        this.politicianRepository = politicianRepository;
    }

    public void importCsv(String csv) {
        Reader in = new InputStreamReader(getClass().getResourceAsStream(csv), Charset.forName("UTF-8"));
        updateRepresentativeFromCsv(in);
    }

    public void importLatest() {
        try {
            Reader reader = getCsvFrom(WALLOON_PARLIAMENT_COMPO_CSV_URL);
            updateRepresentativeFromCsv(reader);
        } catch (IOException e) {
            LOGGER.error("Unable to retrieve the representatives from the walloon parliament", e);
        }
    }

    private Reader getCsvFrom(final String csvUrl) throws IOException {
        URL csvDocument = new URL(csvUrl);
        return new BufferedReader(new InputStreamReader(csvDocument.openStream(), Charset.forName("UTF-8")));
    }

    private void updateRepresentativeFromCsv(final Reader csv) {
        Iterable<CSVRecord> records;
        try {
            records = CSVFormat.EXCEL.withDelimiter(';').withHeader().parse(csv);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (CSVRecord record : records) {
            String name = record.get("Nom et prénom");
            String party = record.get("Parti");
            String address = record.get("Adresse");
            String postalCode = record.get("CP");
            String locality = record.get("Localité");
            String phone = record.get("Tél.");
            String fax = record.get("Fax.");
            String email = record.get("e-mail");
            String site = record.get("Site");
            politicianRepository.upsertPolitician(name, party, address, postalCode, locality, phone, fax, email, site, PersonFunction.DEPUTY, AssemblyEnum.WAL);
        }
    }
}
