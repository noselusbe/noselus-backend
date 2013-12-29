package be.noselus.repository;

import be.noselus.NosElusModule;
import be.noselus.model.Question;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class QuestionRepositoryTest {

    private QuestionRepository repo;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new NosElusModule());
        repo = injector.getInstance(QuestionRepositoryInDatabase.class);
    }

    @Test
    public void containData() {
        final List<Question> questions = repo.getQuestions();
        Assert.assertTrue(questions.size() == 50);
    }

    @Test
    public void findByKeyWord() {
        final List<Question> questions = repo.searchByKeyword("Flandre", "Wallonie");
        Assert.assertTrue(questions.size() > 0);
    }
}
