package be.noselus.model;

import org.junit.Assert;
import org.junit.Test;

public class PersonTest {

	@Test
	public void full() {
		Person person = new Person("Paul FURLAN");
		Assert.assertEquals("Paul", person.firstName);
		Assert.assertEquals("FURLAN", person.name);
	}
	
	@Test
	public void detailled() {
		Person person = new Person("FURLAN", "Paul");
		Assert.assertEquals("Paul", person.firstName);
		Assert.assertEquals("FURLAN", person.name);
	}
	
	@Test
	public void toFullText() {
		Person person = new Person("FURLAN", "Paul");
		Assert.assertEquals("Paul FURLAN", person.toString());
	}
	
}
