package be.noselus.repository;

import be.noselus.model.Person;
import be.noselus.model.PersonSmall;

import java.util.List;

public interface DeputyRepository {

    List<Person> getDeputies();

    List<Person> getFullDeputyByName(String name);
    List<PersonSmall> getDeputyByName(String name);
}
