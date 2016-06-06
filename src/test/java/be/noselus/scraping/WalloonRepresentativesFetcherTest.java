package be.noselus.scraping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@RunWith(MockitoJUnitRunner.class)
public class WalloonRepresentativesFetcherTest {

    private final WalloonRepresentativesFetcher importer = new WalloonRepresentativesFetcher();

    @Test
    public void findAPerson() throws IOException {
        final List<WalloonRepresentativeDocument> persons = importer.searchFor("DEFRANG-FIRKET Virginie");
        assertThat(persons, not(empty()));
        assertThat(persons, hasSize(1));
        assertThat(persons.get(0).getName(), is("DEFRANG-FIRKET Virginie"));
    }

    @Test
    public void findAAntoine() throws IOException {
        final List<WalloonRepresentativeDocument> persons = importer.searchFor("ANTOINE André");
        assertThat(persons, not(empty()));
        assertThat(persons, hasSize(1));
        assertThat(persons.get(0).getName(), is("ANTOINE André"));
    }

    @Test
    public void findWithSpecialCharacterInMiddleOfName() throws IOException {
        final List<WalloonRepresentativeDocument> persons = importer.searchFor("BONNI Véronique");
        assertThat(persons, not(empty()));
        assertThat(persons, hasSize(1));
        assertThat(persons.get(0).getName(), is("BONNI Véronique"));
    }


}