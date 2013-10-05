package be.noselus.repository;

import be.noselus.db.DatabaseHelper;
import be.noselus.model.Person;
import be.noselus.model.PersonFunction;
import be.noselus.model.PersonSmall;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PoliticianRepositoryInDatabase implements PoliticianRepository {

    private static final Logger logger = LoggerFactory.getLogger(PoliticianRepositoryInDatabase.class);
    private List<Person> politicians;

    @Override
    public List<Person> getPoliticians() {
        if (politicians == null) {
            initPoliticians();
        }
        return politicians;
    }

    private void initPoliticians() {
        try {
        	Connection db = DatabaseHelper.openConnection(false, true);
        	PreparedStatement stat = db.prepareStatement("SELECT * FROM person;");
        	
        	stat.execute();
        	
        	politicians = new ArrayList<Person>();
        	while (stat.getResultSet().next()) {
        		
        		int id = stat.getResultSet().getInt("id");
        		String full_name = stat.getResultSet().getString("full_name");
        		String party = stat.getResultSet().getString("party");
        		String address = stat.getResultSet().getString("address");
        		String postal_code = stat.getResultSet().getString("postal_code");
        		String town = stat.getResultSet().getString("town");
        		String phone = stat.getResultSet().getString("phone");
        		String fax = stat.getResultSet().getString("fax");
        		String email = stat.getResultSet().getString("email");
        		String site = stat.getResultSet().getString("site");
        		PersonFunction function = PersonFunction.valueOf(stat.getResultSet().getString("function"));
        		int assembly_id = stat.getResultSet().getInt("assembly_id");

                List<Integer> questions = Collections.emptyList();

        		Person person = new Person(id, full_name, party, address, postal_code, town, phone, fax, email, site, function, assembly_id, questions);
        		politicians.add(person);
        	}
        	
        	stat.close();
        	db.close();

        } catch (SQLException | ClassNotFoundException e) {
           logger.error("Error loading person from DB", e);
        }
        
        final ArrayList<Person> persons = Lists.newArrayList(politicians);
        politicians = persons;
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
