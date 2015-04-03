package be.noselus.scraping;

import be.noselus.model.AssemblyEnum;
import be.noselus.model.PersonFunction;
import be.noselus.repository.PoliticianRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class WalloonRepresentativesCsvImporter {

    private final PoliticianRepository politicianRepository;

    @Inject
    public WalloonRepresentativesCsvImporter(final PoliticianRepository politicianRepository) {
        this.politicianRepository = politicianRepository;
    }

    public void importCsv(String csv){
        Reader in = new InputStreamReader(getClass().getResourceAsStream(csv), Charset.forName("UTF-8"));
        Iterable<CSVRecord> records;
        try {
            records = CSVFormat.EXCEL.withDelimiter(';').withHeader().parse(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (CSVRecord record : records) {
            String name = record.get("Nom");
            String party = record.get("Parti");
            String address = record.get("Adresse");
            String postalCode = record.get("CP");
            String locality = record.get("Localite");
            String phone = record.get("Tel.");
            String fax = record.get("Fax.");
            String email = record.get("e-mail");
            String site = record.get("Site");
            politicianRepository.upsertPolitician(name,party,address,postalCode, locality, phone, fax, email,site, PersonFunction.DEPUTY, AssemblyEnum.WAL);
        }
    }
}