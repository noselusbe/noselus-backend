package be.noselus.tools;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class GeocodeTest {

	@Test
	public void geocode() throws IOException {
		String value = Geocode.lookup(50.839812952485, 4.31287655636466);
		Assert.assertEquals("Birminghamstraat 386, 1070 Anderlecht, Belgium", value);
	}
	
	@Test
	public void reverse_geocode() throws IOException {
		Map<String, Double> value = Geocode.lookup("Birminghamstraat 386, 1070 Anderlecht, Belgium");
		Assert.assertEquals(50.839812952485, value.get("latitude"), 0.01);
		Assert.assertEquals(4.31287655636466, value.get("longitude"), 0.01);
	}
	
}
