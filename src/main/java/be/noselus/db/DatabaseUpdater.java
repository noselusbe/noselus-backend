package be.noselus.db;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.structure.DatabaseObject;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUpdater {

    public void update() {
        try {
            Thread currentThread = Thread.currentThread();
            ClassLoader classLoader = currentThread.getContextClassLoader();
            ResourceAccessor accessor = new ClassLoaderResourceAccessor(classLoader);
            Connection connection = DatabaseHelper.getInstance().getConnection(true, false);
            JdbcConnection jdbcConnection = new JdbcConnection(connection);

            Database database = null;
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);

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
            liq.update("");
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
