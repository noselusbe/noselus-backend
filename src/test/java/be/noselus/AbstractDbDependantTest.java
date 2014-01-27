package be.noselus;

import be.noselus.db.DatabaseUpdater;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;

public abstract class AbstractDbDependantTest {
    protected static DataSource dataSource;

    @ClassRule
    public static ExternalResource resource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            AbstractDbDependantTest.dataSource = ds;
            DatabaseUpdater dbUpdate = new DatabaseUpdater(ds);
            dbUpdate.update();
        }

        @Override
        protected void after() {
        }
    };
}
