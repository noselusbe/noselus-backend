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
import java.util.concurrent.atomic.AtomicInteger;

public class PoliticianRepositoryInMemory implements PoliticianRepository {

    private static final Logger logger = LoggerFactory.getLogger(PoliticianRepositoryInMemory.class);
    private List<Person> politicians;

    @Override
    public List<Person> getPoliticians() {
        if (politicians == null) {

            initPoliticians();
        }
        return politicians;
    }

    private AtomicInteger politicanIdGenerator = new AtomicInteger();

    private void initPoliticians() {
        try {
            URL url = getClass().getResource("/liste_parl_pw-2013-10-04.csv");
            File file = new File(url.toURI());
            politicians = Files.readLines(file, Charsets.UTF_8, new LineProcessor<List<Person>>() {

                final List<Person> result = new ArrayList<>();
                int treatedLine = 0;


                @Override
                public boolean processLine(final String s) throws IOException {
                    treatedLine++;
                    if (treatedLine == 1) { //skip first line with the titles
                        return true;
                    }
                    final String line = s.replace(" ", " ") + " ";//Some weird character, is not a normal space, replace by space
                    final String[] fields = line.split(";");

                    final String site = fields[8].trim();

                    Person person = new Person(politicanIdGenerator.getAndIncrement(), fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], site, PersonFunction.DEPUTY, 0);
                    result.add(person);
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

    @Override
    public List<Person> getFullPoliticianByName(final String name) {
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

        Collection<Person> foundPerson = Collections2.filter(getPoliticians(), hasName);
        return Lists.newArrayList(foundPerson);

    }

    @Override
    public List<PersonSmall> getPoliticianByName(final String name) {
        final List<Person> fullDeputyByName = getFullPoliticianByName(name);
        return Lists.transform(fullDeputyByName, new Function<Person, PersonSmall>() {
            @Override
            public PersonSmall apply(final be.noselus.model.Person input) {
                return PersonSmall.fromPerson(input);
            }
        });
    }

    @Override
    public Person getPoliticianById(final int id) {
        Predicate<Person> withId = new Predicate<Person>() {
            public boolean apply(Person p) {
                return p != null && (p.id == id);
            }
        };

        Collection<Person> foundPerson = Collections2.filter(getPoliticians(), withId);
        return foundPerson.iterator().next();
    }

}
