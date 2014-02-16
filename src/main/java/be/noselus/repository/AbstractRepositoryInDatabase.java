package be.noselus.repository;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Class containing common tools for all repositories that will get their info from the database.
 */
public abstract class AbstractRepositoryInDatabase {

    protected final DataSource dataSource;

    @Inject
    public AbstractRepositoryInDatabase(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
