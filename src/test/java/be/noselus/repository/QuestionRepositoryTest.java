package be.noselus.repository;

import be.noselus.model.Question;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class QuestionRepositoryTest {

    private QuestionRepository repo = new QuestionRepositoryInDatabase();

    @Test
    public void containData(){
        final List<Question> questions = repo.getQuestions();
        Assert.assertTrue(questions.size() == 50);
    }

    @Test
    public void findByKeyWord(){
        final List<Question> questions = repo.searchByKeyword("Flandre", "Wallonie");
        Assert.assertTrue(questions.size() > 0);
    }
}
