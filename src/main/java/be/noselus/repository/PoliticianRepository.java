package be.noselus.repository;

import be.noselus.model.Person;
import be.noselus.model.PersonSmall;

import java.util.List;

public interface PoliticianRepository {

    List<Person> getPoliticians();

    List<Person> getFullPoliticianByName(String name);
    List<PersonSmall> getPoliticianByName(String name);

    Person getPoliticianById(int i);

    void upsertPolitician(Person representative);
}
