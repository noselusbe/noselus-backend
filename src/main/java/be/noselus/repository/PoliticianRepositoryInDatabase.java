package be.noselus.repository;

import be.noselus.model.*;
import be.noselus.pictures.PictureManager;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton
public class PoliticianRepositoryInDatabase implements PoliticianRepository, ResultSetMapper<Person> {

    private final QueryRunnerAdapter queryRunner;
    private final AssemblyRepository assemblyRepository;
    private final PictureManager pictureManager;

    private final List<Person> politicians = new ArrayList<>();
    private boolean dirty = false;

    @Inject
    public PoliticianRepositoryInDatabase(final DataSource dataSource, final AssemblyRepository assemblyRepository,
                                          final PictureManager pictureManager) {
        this.assemblyRepository = assemblyRepository;
        this.pictureManager = pictureManager;
        this.queryRunner = new QueryRunnerAdapter(dataSource);
    }

    @Override
    public List<Person> getPoliticians() {
        synchronized (politicians){
            if (dirty){
                politicians.clear();
            }
            if (politicians.isEmpty()) {
                politicians.addAll(initPoliticians());
            }
        }
        return Collections.unmodifiableList(politicians);
    }

    private List<Person> initPoliticians() {
        return queryRunner.query("SELECT person.* FROM person"
                + " WHERE person.id != 0;", new MapperBasedResultSetListHandler<>(this));
    }

    @Override
    public List<Person> getFullPoliticianByName(final String name) {
        Predicate<Person> hasName = p -> {
            final int endIndex = name.lastIndexOf(' ');
            final String lastName;
            if (endIndex == -1) {
                lastName = name;
            } else {
                lastName = name.substring(0, endIndex).replace(" ", " ");
            }
            return p != null && (name.equals(p.fullName) || p.fullName.contains(name) || p.fullName.contains(lastName));
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
        Collection<Person> foundPerson = Collections2.filter(getPoliticians(), p -> p != null && (p.id == id));
        if (foundPerson.isEmpty()) {
            return null;
        }
        return foundPerson.iterator().next();
    }

    @Override
    public void updatePolitician(final Person representative) {
        queryRunner.update("UPDATE PERSON SET assembly_id = ? WHERE id = ?",
                representative.assemblyId, representative.id
        );
        dirty = true;
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
            if (politician.fullName.trim().equalsIgnoreCase(name.trim())){
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
        dirty = true;
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

        final Person person = new Person(id, full_name, party, address, postal_code,
                town, phone, fax, email, site, function, assemblyId,
                questions, assembly, latitude, longitude);
        if (pictureManager.hasPicture(id)){
            person.picture = new Link("/politicians/" + id + "/picture");
        }
        return person;
    }
}
