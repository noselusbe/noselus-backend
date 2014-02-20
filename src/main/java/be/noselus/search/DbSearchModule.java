package be.noselus.search;

import be.noselus.repository.QuestionRepository;
import be.noselus.repository.QuestionRepositoryInDatabase;
import com.google.inject.AbstractModule;

public class DbSearchModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(QuestionRepository.class).to(QuestionRepositoryInDatabase.class);
    }
}
