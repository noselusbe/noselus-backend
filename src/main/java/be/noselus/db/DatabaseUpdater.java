package be.noselus.db;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.structure.DatabaseObject;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Handle all responsibilities regarding database update management.
 * <p/>
 * Based on https://github.com/Athou/commafeed/blob/master/src/main/java/com/commafeed/backend/startup/DatabaseUpdater.java
 */
public class DatabaseUpdater {

    private final DatabaseHelper dbHelper;

    @Inject
    public DatabaseUpdater(final DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Update the database by executing all the required change sets.
     */
    public void update() {
        try (Connection connection = dbHelper.getConnection(true, false);) {
            Thread currentThread = Thread.currentThread();
            ClassLoader classLoader = currentThread.getContextClassLoader();
            ResourceAccessor accessor = new ClassLoaderResourceAccessor(classLoader);

            JdbcConnection jdbcConnection = new JdbcConnection(connection);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);

            if (database instanceof PostgresDatabase) {
                database = new PostgresDatabase() {
                    @Override
                    public String escapeObjectName(String objectName, Class<? extends DatabaseObject> objectType) {
                        return objectName;
                    }
                };
                database.setConnection(jdbcConnection);
            }
            Liquibase liq = new Liquibase("db/changelog-master.xml", accessor, database);
            liq.clearCheckSums();
            liq.update("");
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
