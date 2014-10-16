package be.noselus.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

//@Ignore("work in progress")
public class WalloonRepresentativeDocumentTest {

    private WalloonRepresentativeDocument representative;


    @Before
    public void  setUp() throws IOException {
        final InputStream in = getClass().getClassLoader().getResourceAsStream("scraping/PW_Henry_Philippe.html");
        Document doc = Jsoup.parse(in, "utf-8", "classpath:scraping/PW_Henry_Philippe.html");
        representative = new WalloonRepresentativeDocument(doc);
    }

    @Test
    public void hasCorrectName(){
        assertEquals("HENRY Philippe", representative.getName());
    }

    @Test
    @Ignore("id coming from url, can't do that in test")
    public void hasCorrectAssemblyRef(){
        assertEquals(105, representative.getId());
    }

    @Test
    public void hasCorrectParty(){
        assertEquals("ECOLO", representative.getParty());
    }

    @Test
    @Ignore("work in progress")
    public void hasCorrectAddress(){
        assertEquals("avenue Marcellin la Garde, 2", representative.getAddress());
    }

    @Test
    @Ignore("work in progress")
    public void hasCorrectPostalCode(){
        assertEquals("4920", representative.getPostalCode());
    }

    @Test
    @Ignore("work in progress")
    public void hasCorrectTown(){
        assertEquals("AYWAILLE", representative.getTown());
    }

    @Test
    @Ignore("work in progress")
    public void hasCorrectPhone(){
        assertEquals("04.232.30.00", representative.getPhone());
    }

    @Test
    public void hasCorrectFax(){
        assertEquals("04.232.30.09", representative.getFax());
    }

    @Test
    public void hasCorrectEmail(){
        assertEquals("p.henry@skynet.be", representative.getEmail());
    }
}