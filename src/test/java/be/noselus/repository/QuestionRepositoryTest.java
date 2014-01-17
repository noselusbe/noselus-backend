package be.noselus.repository;

import be.noselus.NosElusModule;
import be.noselus.dto.PartialResult;
import be.noselus.model.Question;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertTrue(questions.size() == 50);
    }

    @Test
    public void findByKeyWord() {
        final List<Question> questions = repo.searchByKeyword("Flandre", "Wallonie");
        assertTrue(!questions.isEmpty());
    }

    @Test
    public void findWithLimitReturnNoMoreThanLimit(){
        final PartialResult<Question> questions = repo.getQuestions(1);
        assertEquals(1, questions.getResults().size());
    }

    @Test
    public void findTheRightResult(){
        final PartialResult<Question> questions = repo.getQuestions(10,1000);
        assertEquals(10, questions.getResults().size());
        for (Question question : questions.getResults()) {
            assertTrue(question.id < 1000);
        }
    }
}
