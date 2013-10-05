package be.noselus.repository;

import be.noselus.model.Person;
import com.google.common.base.Optional;

import java.util.List;

public interface DeputyRepository {

    List<Person> getDeputies();

    Optional<Person> getDeputyByName(String name);
}
