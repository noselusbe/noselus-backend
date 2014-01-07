package be.noselus.model;

import org.junit.Assert;
import org.junit.Test;


public class PersonSmallTest {

	@Test
	public void full() {
		PersonSmall person = new PersonSmall("Paul FURLAN");
		Assert.assertEquals("Paul FURLAN", person.fullName);
		Assert.assertEquals(0, person.id.intValue());
	}
	
}
