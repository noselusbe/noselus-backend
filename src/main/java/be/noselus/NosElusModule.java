package be.noselus;

import be.noselus.repository.*;
import com.google.inject.AbstractModule;

public class NosElusModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AssemblyRegistry.class).to(AssemblyRegistryInDatabase.class);
        bind(PoliticianRepository.class).to(PoliticianRepositoryInDatabase.class);
        bind(QuestionRepository.class).to(QuestionRepositoryInDatabase.class);
    }
}
