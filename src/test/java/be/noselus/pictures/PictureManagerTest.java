package be.noselus.pictures;

import be.noselus.AbstractDbDependantTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class PictureManagerTest extends AbstractDbDependantTest {

    private PictureManager pictureManager;

    @Before
    public void setUp() {
        pictureManager = new PictureManager(AbstractDbDependantTest.dataSource);
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
