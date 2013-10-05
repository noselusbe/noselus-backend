package be.noselus.repository;

import be.noselus.model.Person;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeputyRepositoryInMemory implements DeputyRepository {

    @Override
    public List<Person> getDeputies() {

        try {
            URL url = getClass().getResource("/liste_parl_pw-2013-10-04.csv");
            File file = new File(url.toURI());
            return Files.readLines(file, Charset.defaultCharset(),new LineProcessor<List<Person>>() {

                final List<Person> result = new ArrayList<>();
                int treatedLine=0;

                @Override
                public boolean processLine(final String s) throws IOException {
                    treatedLine++;
                    if (treatedLine == 1){ //skip first line with the titles
                        return true;
                    }
                    final String line = s + " ";
                    final String[] fields = line.split(";");

                    final String site = fields[8].trim();

                    Person person = new Person(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], site);
                    result.add(person);
                    return true;
                }

                @Override
                public List<Person> getResult() {
                    return Collections.unmodifiableList(result);  //To change body of implemented methods use File | Settings | File Templates.
                }
            });

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Collections.emptyList();
    }

}
