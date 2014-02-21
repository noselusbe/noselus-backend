package be.noselus.repository;

import be.noselus.model.Assembly;

import java.util.List;

public interface AssemblyRegistry {

    Assembly findId(int id);

    List<Assembly> getAssemblies();
}
