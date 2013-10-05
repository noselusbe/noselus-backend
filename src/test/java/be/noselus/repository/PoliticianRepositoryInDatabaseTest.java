package be.noselus.repository;

import be.noselus.model.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


public class PoliticianRepositoryInDatabaseTest {

	List<Person> store;
	
	@Before
	public void init() {
		store = new PoliticianRepositoryInDatabase().getPoliticians();
	}
	
	@Test
	public void size() {
		Assert.assertTrue(store.size() > 0);
	}
}
