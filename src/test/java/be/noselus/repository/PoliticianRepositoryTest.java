package be.noselus.repository;

import be.noselus.AbstractDbDependantTest;
import be.noselus.model.AssemblyEnum;
import be.noselus.model.Person;
import be.noselus.model.PersonFunction;
import be.noselus.model.PersonSmall;
import be.noselus.pictures.PictureManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PoliticianRepositoryTest extends AbstractDbDependantTest {

    PoliticianRepository repo;

    @Before
    public void init() {
        repo = new PoliticianRepositoryInDatabase(AbstractDbDependantTest.dataSource,
                new AssemblyRepositoryInDatabase(AbstractDbDependantTest.dataSource),
                new PictureManager(AbstractDbDependantTest.dataSource));
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

    @Test
    public void returnAllDeputies() {
        final List<Person> deputies = repo.getPoliticians();
        assertEquals(245, deputies.size());
    }

    @Test
    public void firstDeputyIsExpected() {
        final List<Person> deputies = repo.getFullPoliticianByName("BARZIN");
        final Person person = deputies.get(0);
        assertEquals("BARZIN Anne", person.fullName);
    }

    @Test
    public void deputyWithNoSite() {
        final List<Person> deputies = repo.getFullPoliticianByName("BASTIN");
        final Person person = deputies.get(0);
        //BASTIN Jean-Paul;cdH;Al'Gofe, 19;4960;G'DOUMONT-MALMEDY;080 79 96 66;087 32 22 69;sec.jpbastin@lecdh.be;
        assertEquals("BASTIN Jean-Paul", person.fullName);
        assertEquals("cdH", person.party);
        assertEquals("Al'Gofe, 19", person.address);
        assertEquals("4960", person.postalCode);
        assertEquals("G'DOUMONT-MALMEDY", person.town);
        assertEquals("080 79 96 66", person.phone);
        assertEquals("087 32 22 69", person.fax);
        assertEquals("sec.jpbastin@lecdh.be", person.email);
        assertEquals("", person.site);
        assertEquals(50.4, person.latitude, 0.1);
        assertEquals(6.0, person.longitude, 0.1);
    }

    @Test
    public void findByName() {
        final List<Person> found = repo.getFullPoliticianByName("KUBLA");
        assertEquals(1, found.size());
        assertEquals("KUBLA Serge", found.get(0).fullName);
    }

    @Test
    public void findSmallByName() {
        final List<PersonSmall> found = repo.getPoliticianByName("ONKELINX");
        assertEquals(1, found.size());
        assertEquals("ONKELINX Alain", found.get(0).fullName);
    }

    @Test
    public void findByAlmostName() {
        final List<PersonSmall> found = repo.getPoliticianByName("KAPOMPOLE Joëlle");
        assertEquals(1, found.size());
    }

    @Test
    @Ignore("db is not cleanedup after this test.")
    public void insertNewPolitician(){
        repo.upsertPolitician("Will Smith", "Rock", "Fresh", "90210","Belair","001",null,null,null, PersonFunction.DEPUTY, AssemblyEnum.WAL);
        final List<PersonSmall> politicianByName = repo.getPoliticianByName("Will Smith");
        boolean willSmithIsFound = false;
        for (PersonSmall personSmall : politicianByName) {
            if (personSmall.fullName.equalsIgnoreCase("Will Smith")){
                willSmithIsFound = true;
            }
        }
        assertTrue("Will smith should be one of the politicians now",willSmithIsFound);
    }
}
