package be.noselus.repository;

import be.noselus.model.Assembly;

import java.util.List;

public interface AssemblyRepository {

    Assembly findId(int id);

    List<Assembly> getAssemblies();
}
