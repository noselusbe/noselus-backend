package be.noselus.repository;

import be.noselus.model.*;
import be.noselus.util.dbutils.MapperBasedResultSetListHandler;
import be.noselus.util.dbutils.QueryRunnerAdapter;
import be.noselus.util.dbutils.ResultSetMapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton
public class PoliticianRepositoryInDatabase implements PoliticianRepository, ResultSetMapper<Person> {

    private final QueryRunnerAdapter queryRunner;
    private final AssemblyRepository assemblyRepository;
    private List<Person> politicians;

    @Inject
    public PoliticianRepositoryInDatabase(final DataSource dataSource, final AssemblyRepository assemblyRepository) {
        this.assemblyRepository = assemblyRepository;
        this.queryRunner = new QueryRunnerAdapter(dataSource);
    }

    @Override
    public synchronized List<Person> getPoliticians() {
        if (politicians == null) {
            politicians = initPoliticians();
        }
        return politicians;
    }

    private List<Person> initPoliticians() {
        return queryRunner.query("SELECT person.* FROM person"
                + " WHERE person.id != 0;", new MapperBasedResultSetListHandler<>(this));
    }

    @Override
    public List<Person> getFullPoliticianByName(final String name) {
        Predicate<Person> hasName = new Predicate<Person>() {
            @Override
            public boolean apply(Person p) {
                final int endIndex = name.lastIndexOf(' ');
                final String lastName;
                if (endIndex == -1) {
                    lastName = name;
                } else {
                    lastName = name.substring(0, endIndex).replace(" ", " ");
                }

                return p != null && (name.equals(p.fullName) || p.fullName.contains(name) || p.fullName.contains(lastName));
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
            @Override
            public boolean apply(Person p) {
                return p != null && (p.id == id);
            }
        };

        Collection<Person> foundPerson = Collections2.filter(getPoliticians(), withId);
        if (foundPerson.isEmpty()) {
            return null;
        }
        return foundPerson.iterator().next();
    }

    @Override
    public void upsertPolitician(final Person representative) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void upsertPolitician(final String name, final String party, final String address, final String postalCode,
                                 final String locality, final String phone, final String fax, final String email,
                                 final String site, final PersonFunction function, final AssemblyEnum assembly) {
        if (!politicianIsPresent(name)){
            insertNewPolitician(name,party,address,postalCode,locality, phone, fax,  email,site, function,  assembly);
            resetCache();
        }
    }

    private boolean politicianIsPresent(final String name) {
        for (Person politician : getPoliticians()) {
            if (politician.fullName.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    private void insertNewPolitician(final String name, final String party, final String address, final String postalCode,
                                     final String locality, final String phone, final String fax, final String email,
                                     final String site, final PersonFunction function, final AssemblyEnum assembly) {

        queryRunner.update("INSERT INTO PERSON(full_name, party, address, postal_code, town, phone, fax, email, site, belong_to_assembly, function)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                name, party, address, postalCode, locality, phone, fax, email, site, assembly.getId(), function.name()
        );
    }

    private void resetCache() {
        politicians = null;
    }

    @Override
    public Person map(final ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String full_name = resultSet.getString("full_name");
        String party = resultSet.getString("party");
        String address = resultSet.getString("address");
        String postal_code = resultSet.getString("postal_code");
        String town = resultSet.getString("town");
        String phone = resultSet.getString("phone");
        String fax = resultSet.getString("fax");
        String email = resultSet.getString("email");
        String site = resultSet.getString("site");
        Integer assemblyId = resultSet.getInt("assembly_id");
        PersonFunction function = PersonFunction.valueOf(resultSet.getString("function"));
        double latitude = resultSet.getDouble("lat");
        double longitude = resultSet.getDouble("long");

        Integer belong_to_assembly_id = resultSet.getInt("belong_to_assembly");
        Assembly assembly = assemblyRepository.findId(belong_to_assembly_id);

        List<Integer> questions = Collections.emptyList();

        return new Person(id, full_name, party, address, postal_code,
                town, phone, fax, email, site, function, assemblyId,
                questions, assembly, latitude, longitude);
    }
}
