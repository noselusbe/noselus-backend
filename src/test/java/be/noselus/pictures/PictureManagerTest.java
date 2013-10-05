package be.noselus.pictures;

import be.noselus.repository.PoliticianRepositoryInDatabase;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;


public class PictureManagerTest {

    final PictureManager pictureManager = new PictureManager(new PoliticianRepositoryInDatabase());

	@Test
	public void getPictureParlement() throws IOException {
		InputStream is = pictureManager.get(77);
		Assert.assertNotNull(is);
		is.close();
	}
	
	@Test
	public void getPictureChamber() throws IOException {
		InputStream is = pictureManager.get(857);
		Assert.assertNotNull(is);
		is.close();
	}
}