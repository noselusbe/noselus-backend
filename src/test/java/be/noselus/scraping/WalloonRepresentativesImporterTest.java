package be.noselus.scraping;

import be.noselus.repository.PoliticianRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
@Ignore("work in progress")
public class WalloonRepresentativesImporterTest {

    @Mock
    public PoliticianRepository politicianRepository;

    private WalloonRepresentativesImporter importer = new WalloonRepresentativesImporter(politicianRepository);

    @Test
    public void bla() throws IOException {

        importer.updateRepresentativeList();

    }

}