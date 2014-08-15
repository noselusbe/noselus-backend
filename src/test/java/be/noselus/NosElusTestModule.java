package be.noselus;

import be.noselus.repository.QuestionRepository;
import be.noselus.repository.QuestionRepositoryInDatabase;
import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class NosElusTestModule extends AbstractModule {

    public static final String TEST_DB = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    @Override
    protected void configure() {
        HikariConfig hkConfig = new HikariConfig();
        hkConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        hkConfig.addDataSourceProperty("URL",TEST_DB);
        hkConfig.setMaximumPoolSize(5);
        DataSource ds = new HikariDataSource(hkConfig);

        bind(DataSource.class).toInstance(ds);
        bind(QuestionRepository.class).to(QuestionRepositoryInDatabase.class);
    }
}
