package be.noselus.repository;

import be.noselus.model.Person;
import be.noselus.model.PersonFunction;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DeputyRepositoryInMemory implements DeputyRepository {

    private List<Person> deputies;

    @Override
    public List<Person> getDeputies() {
        if (deputies == null){

        try {
            URL url = getClass().getResource("/liste_parl_pw-2013-10-04.csv");
            File file = new File(url.toURI());
            deputies = Files.readLines(file, Charset.defaultCharset(),new LineProcessor<List<Person>>() {

                final List<Person> result = new ArrayList<>();
                int treatedLine=0;
                int idCounter = 0;

                @Override
                public boolean processLine(final String s) throws IOException {
                    treatedLine++;
                    if (treatedLine == 1){ //skip first line with the titles
                        return true;
                    }
                    final String line = s + " ";
                    final String[] fields = line.split(";");

                    final String site = fields[8].trim();

                    Person person = new Person(idCounter,fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], site, PersonFunction.DEPUTY);
                    result.add(person);
                    idCounter++;
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

        }
        return deputies;
    }

    @Override
    public Optional<Person> getDeputyByName(final String name) {
        Predicate<Person> hasName = new Predicate<Person>() {
            public boolean apply(Person p) {
                return p != null && (name.equals(p.full_name) || p.full_name.contains(name));
            }
        };

        Collection<Person> foundPerson = Collections2.filter(getDeputies(), hasName);

        if (foundPerson.size() == 1){
            return Optional.of(foundPerson.iterator().next());
        } else if (foundPerson.size() > 1){
            //trouble in paradise;
        }
        return Optional.absent();
    }

}
