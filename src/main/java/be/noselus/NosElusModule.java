package be.noselus;

import be.noselus.db.DatabaseHelper;
import be.noselus.db.DatabaseUpdater;
import be.noselus.db.DbConfig;
import be.noselus.repository.*;
import be.noselus.service.PoliticianRoutes;
import be.noselus.service.QuestionRoutes;
import be.noselus.service.Routes;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class NosElusModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AssemblyRegistry.class).to(AssemblyRegistryInDatabase.class);
        bind(PoliticianRepository.class).to(PoliticianRepositoryInDatabase.class);
        bind(QuestionRepository.class).to(QuestionRepositoryInDatabase.class);
        Multibinder<Routes> routesMultibinder = Multibinder.newSetBinder(binder(), Routes.class);
        routesMultibinder.addBinding().to(QuestionRoutes.class);
        routesMultibinder.addBinding().to(PoliticianRoutes.class);
        requireBinding(DatabaseHelper.class);
        requireBinding(DbConfig.class);
        requireBinding(DatabaseUpdater.class);
    }
}
