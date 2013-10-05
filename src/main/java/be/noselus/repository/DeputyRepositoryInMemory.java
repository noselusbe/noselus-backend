package be.noselus.repository;

import be.noselus.model.Person;
import be.noselus.model.PersonFunction;
import be.noselus.model.PersonSmall;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DeputyRepositoryInMemory implements DeputyRepository {

    private static final Logger logger = LoggerFactory.getLogger(DeputyRepositoryInMemory.class);
    private List<Person> deputies;

    @Override
    public List<Person> getDeputies() {
        if (deputies == null) {

            try {
                URL url = getClass().getResource("/liste_parl_pw-2013-10-04.csv");
                File file = new File(url.toURI());
                deputies = Files.readLines(file, Charsets.UTF_8, new LineProcessor<List<Person>>() {

                    final List<Person> result = new ArrayList<>();
                    int treatedLine = 0;
                    int idCounter = 0;

                    @Override
                    public boolean processLine(final String s) throws IOException {
                        treatedLine++;
                        if (treatedLine == 1) { //skip first line with the titles
                            return true;
                        }
                        final String line = s.replace(" ", " ") + " ";//Some weird character, is not a normal space, replace by space
                        final String[] fields = line.split(";");

                        final String site = fields[8].trim();

                        Person person = new Person(idCounter, fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], site, PersonFunction.DEPUTY);
                        result.add(person);
                        idCounter++;
                        return true;
                    }

                    @Override
                    public List<Person> getResult() {
                        return Collections.unmodifiableList(result);  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });

            } catch (IOException|URISyntaxException e) {
               logger.error("Error reading file", e);
            }

        }
        return deputies;
    }

    @Override
    public List<Person> getFullDeputyByName(final String name) {
        Predicate<Person> hasName = new Predicate<Person>() {
            public boolean apply(Person p) {
                final int endIndex = name.lastIndexOf(" ");
                final String lastName;
                if (endIndex == -1){
                    lastName = name;
                } else {
                    lastName = name.substring(0, endIndex);
                }

                return p != null && (name.equals(p.full_name) || p.full_name.contains(name) || p.full_name.contains(lastName));
            }
        };

        Collection<Person> foundPerson = Collections2.filter(getDeputies(), hasName);
        return Lists.newArrayList(foundPerson);

    }

    @Override
    public List<PersonSmall> getDeputyByName(final String name) {
        final List<Person> fullDeputyByName = getFullDeputyByName(name);
        return Lists.transform(fullDeputyByName, new Function<Person, PersonSmall>() {
            @Override
            public PersonSmall apply(final be.noselus.model.Person input) {
                return PersonSmall.fromPerson(input);
            }
        });
    }

    @Override
    public Person getDeputyById(final int id) {
        Predicate<Person> withId = new Predicate<Person>() {
            public boolean apply(Person p) {
                return p != null && (p.id == id);
            }
        };

        Collection<Person> foundPerson = Collections2.filter(getDeputies(), withId);
        return foundPerson.iterator().next();
    }

}
