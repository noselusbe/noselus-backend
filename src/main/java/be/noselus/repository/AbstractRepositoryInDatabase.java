package be.noselus.repository;

import be.noselus.db.DatabaseHelper;

/**
 * Class containing common tools for all repositories that will get their info from the database.
 */
public abstract class AbstractRepositoryInDatabase {

    protected final DatabaseHelper dbHelper;

    public AbstractRepositoryInDatabase(final DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
}
