package be.noselus.repository;

import be.noselus.model.Person;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class DeputyRepositoryTest {

    private final DeputyRepository repo = new DeputyRepositoryStub();

    @Test
    public void returnAllDeputies(){
       final List<Person> deputies = repo.getDeputies();
       assertEquals(74, deputies.size());
    }

    @Test
    public void firstDeputyIsExpected(){
        final List<Person> deputies = repo.getDeputies();
        final Person person = deputies.get(0);
        assertEquals("BARZIN Anne", person.name);
    }
}
