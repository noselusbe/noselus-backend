package be.noselus.repository;

import be.noselus.model.Assembly;

public interface AssemblyRegistry {

    Assembly findId(int id);
}
