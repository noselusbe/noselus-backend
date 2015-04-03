package be.noselus;

import be.noselus.db.DatabaseUpdater;
import be.noselus.db.DbConfig;
import be.noselus.repository.AssemblyRepository;
import be.noselus.repository.AssemblyRepositoryInDatabase;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.PoliticianRepositoryInDatabase;
import be.noselus.service.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.sql.DataSource;

public class NosElusModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(NosElusModule.class);

    @Override
    protected void configure() {
        bind(AssemblyRepository.class).to(AssemblyRepositoryInDatabase.class);
        bind(PoliticianRepository.class).to(PoliticianRepositoryInDatabase.class);
        Multibinder<Routes> routesMultibinder = Multibinder.newSetBinder(binder(), Routes.class);
        routesMultibinder.addBinding().to(QuestionRoutes.class);
        routesMultibinder.addBinding().to(PoliticianRoutes.class);
        routesMultibinder.addBinding().to(AssembliesRoutes.class);
        requireBinding(DataSource.class);
        requireBinding(DbConfig.class);
        requireBinding(DatabaseUpdater.class);
        requireBinding(Filters.class);
    }

    @Provides
    @Singleton
    DbConfig getDbConfig() {
        return new DbConfig().invoke();
    }

    @Provides
    @Singleton
    DataSource getDataSource(DbConfig config) {
        HikariConfig hkConfig = new HikariConfig();
        hkConfig.setDriverClassName(config.getDriver());
        hkConfig.setJdbcUrl(config.getUrl());
        hkConfig.setUsername(config.getUsername());
        hkConfig.setPassword(config.getPassword());
        hkConfig.setMaximumPoolSize(5);
        hkConfig.setConnectionTestQuery("SELECT 1");
        HikariDataSource hikariDataSource = new HikariDataSource(hkConfig);
        hikariDataSource.setConnectionTestQuery("SELECT 1");
        LOGGER.info("HikariCPConfig [connectionTestQuery{}]", hikariDataSource.getConnectionTestQuery());
        return hikariDataSource;
    }

}
