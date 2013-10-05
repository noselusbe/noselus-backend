package be.noselus.repository;

import be.noselus.model.Person;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class DeputyRepositoryTest {

    private final DeputyRepository repo = new DeputyRepositoryInMemory();

    @Test
    public void returnAllDeputies(){
       final List<Person> deputies = repo.getDeputies();
       assertEquals(74, deputies.size());
    }

    @Test
    public void firstDeputyIsExpected(){
        final List<Person> deputies = repo.getDeputies();
        final Person person = deputies.get(0);
        assertEquals("BARZIN Anne", person.full_name);
    }
    @Test
    public void deputyWithNoSite(){
        final List<Person> deputies = repo.getDeputies();
        final Person person = deputies.get(1);
        //BASTIN Jean-Paul;cdH;Al'Gofe, 19;4960;G'DOUMONT-MALMEDY;080 79 96 66;087 32 22 69;sec.jpbastin@lecdh.be;
        assertEquals("BASTIN Jean-Paul", person.full_name);
        assertEquals("cdH", person.party);
        assertEquals("Al'Gofe, 19", person.address);
        assertEquals("4960", person.postal_code);
        assertEquals("G'DOUMONT-MALMEDY", person.town);
        assertEquals("080 79 96 66", person.phone);
        assertEquals("087 32 22 69", person.fax);
        assertEquals("sec.jpbastin@lecdh.be", person.email);
        assertEquals("", person.site);

    }
}
