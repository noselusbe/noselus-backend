package be.noselus.repository;

import be.noselus.AbstractDbDependantTest;
import be.noselus.model.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class PoliticianRepositoryInDatabaseTest extends AbstractDbDependantTest {

    PoliticianRepository repo;

    @Before
    public void init() {
        repo = new PoliticianRepositoryInDatabase(AbstractDbDependantTest.dbHelper);
    }

    @Test
    public void size() {
        Assert.assertTrue(repo.getPoliticians().size() > 0);
    }

    @Test
    public void data() {
        final Person person = repo.getPoliticianById(87);
        assertEquals("COLLIGNON Christophe", person.fullName);
        assertEquals("PS", person.party.trim());
        assertEquals("rue du Marché, 45", person.address);
        assertEquals("4500", person.postalCode.trim());
        assertEquals("HUY", person.town);
        assertEquals("085 31 82 00", person.phone.trim());
        assertEquals("085 31 80 99", person.fax.trim());
        assertEquals("contact@christophe-collignon.be", person.email);
        assertEquals("www.christophe-collignon.be", person.site);
        assertEquals(50.5181351, person.latitude, 0.01);
        assertEquals(5.2443019, person.longitude, 0.01);
    }
}
