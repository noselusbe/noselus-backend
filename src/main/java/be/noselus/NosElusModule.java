package be.noselus;

import be.noselus.db.DatabaseUpdater;
import be.noselus.db.DbConfig;
import be.noselus.repository.AssemblyRepository;
import be.noselus.repository.AssemblyRepositoryInDatabase;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.PoliticianRepositoryInDatabase;
import be.noselus.service.AssembliesRoutes;
import be.noselus.service.PoliticianRoutes;
import be.noselus.service.QuestionRoutes;
import be.noselus.service.Routes;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Singleton;
import javax.sql.DataSource;

public class NosElusModule extends AbstractModule {

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
        return new HikariDataSource(hkConfig);
    }

}
