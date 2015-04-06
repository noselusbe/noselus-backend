package be.noselus.repository;

import be.noselus.model.AssemblyEnum;
import be.noselus.model.Person;
import be.noselus.model.PersonFunction;
import be.noselus.model.PersonSmall;

import java.util.List;

public interface PoliticianRepository {

    List<Person> getPoliticians();

    List<Person> getFullPoliticianByName(String name);

    List<PersonSmall> getPoliticianByName(String name);

    Person getPoliticianById(int i);

    void updatePolitician(Person representative);

    void upsertPolitician(String name, String party, String address, String postalCode, String locality, String phone, String fax, String email, String site, final PersonFunction deputy, final AssemblyEnum wal);
}
