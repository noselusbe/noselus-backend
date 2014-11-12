package be.noselus.scraping;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class WalloonRepresentativeCsvImporter {

    @Test
    public void bla() throws IOException {
        Reader in = new InputStreamReader(getClass().getResourceAsStream("/db/liste_parl_pw-2014-10-16.csv"));
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';').withHeader().parse(in);
        String result = "";
        for (CSVRecord record : records) {
            String name = record.get("Nom et prénom");
            String party = record.get("Parti");
            String address = record.get("Adresse");
            String cp = record.get("CP");
            String locality = record.get("Localité");
            String tel = record.get("Tél.");
            String fax = record.get("Fax.");
            String email = record.get("e-mail");
            String site = record.get("Site");
            result += name + " (" + party + ") " + address + cp + locality + tel + fax + email + site +"\r\n";
        }
        System.out.println(result);
    }
}
