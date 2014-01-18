package be.noselus;

import be.noselus.db.DatabaseHelper;
import be.noselus.db.DatabaseUpdater;
import be.noselus.db.DbConfig;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;

public abstract class AbstractDbDependantTest {
    protected static DatabaseHelper dbHelper;

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            dbHelper = new DatabaseHelper(new DbConfig("org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", null, null));
            dbHelper.start();
            DatabaseUpdater dbUpdate = new DatabaseUpdater(dbHelper);
            dbUpdate.update();
        }

        @Override
        protected void after() {
//            dbHelper.stop();
        }
    };
}
