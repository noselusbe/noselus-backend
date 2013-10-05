package be.noselus.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import be.noselus.model.Person;


public class PoliticianRepositoryInDatabaseTest {

	List<Person> store;
	
	@Before
	public void init() {
		store = new PoliticianRepositoryInDatabase().getPoliticians();
	}
	
	@Test
	public void size() {
		Assert.assertEquals(82, store.size());
	}
}
