package be.noselus.pictures;

import be.noselus.NosElusModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class PictureManagerTest {

    private PictureManager pictureManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new NosElusModule());
        pictureManager = injector.getInstance(PictureManager.class);
        pictureManager.start();
    }

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
