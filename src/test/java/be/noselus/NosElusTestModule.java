package be.noselus;

import com.google.inject.AbstractModule;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class NosElusTestModule extends AbstractModule {

    public static final String TEST_DB = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    @Override
    protected void configure() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(TEST_DB);
        bind(DataSource.class).toInstance(ds);
    }
}
