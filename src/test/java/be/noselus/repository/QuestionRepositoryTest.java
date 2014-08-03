package be.noselus.repository;

import be.noselus.AbstractDbDependantTest;
import be.noselus.NosElusTestModule;
import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Assembly;
import be.noselus.model.Question;
import com.google.common.base.Optional;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.junit.Assert.*;

public class QuestionRepositoryTest extends AbstractDbDependantTest {

    private QuestionRepository repo;

    @Before
    public void setUp() {
        AssemblyRepository assemblies = new AssemblyRepositoryInDatabase(AbstractDbDependantTest.dataSource);
        Operation operation =
                sequenceOf(
                        deleteAllFrom("WRITTEN_QUESTION"),
                        insertInto("WRITTEN_QUESTION")
                                .withDefaultValue("DATE_ASKED", Date.valueOf("2014-08-12"))
                                .withDefaultValue("ASSEMBLY_ID", 1)
                                .columns("ID", "TITLE", "ASSEMBLY_REF", "QUESTION_TEXT", "CREATED_AT", "DATE_ANSWER")
                                .values(1L, "Flandre", "45", "Question text", null, Date.valueOf("2010-06-17"))
                                .values(2L, "Wallonie", "32", "Question text", Date.valueOf("2014-08-02"), null)
                                .values(536L, "Question 536", "153", "Question text", Date.valueOf("2014-05-12"), Date.valueOf("2014-08-12"))
                                .build());

        DbSetup dbSetup = new DbSetup(new DriverManagerDestination(NosElusTestModule.TEST_DB, null, null), operation);
        dbSetup.launch();
        repo = new QuestionRepositoryInDatabase(assemblies, AbstractDbDependantTest.dataSource);
    }

    @Test
    public void findByKeyWord() {
        final List<Question> questions = repo.getQuestions(new SearchParameter(), Optional.<Integer>absent(), "Flandre", "Wallonie").getResults();
        assertTrue(!questions.isEmpty());
    }

    @Test
    public void findByKeyWordWithFirstElementSpecified() {
        final List<Question> questions = repo.getQuestions(new SearchParameter(1, 1), Optional.<Integer>absent(), "Flandre", "Wallonie").getResults();
        assertEquals(1, questions.size());
        assertEquals((Integer) 1, questions.get(0).id);
    }

    @Test
    public void findWithLimitReturnNoMoreThanLimit() {
        final PartialResult<Question> questions = repo.getQuestions(new SearchParameter(1), Optional.<Integer>absent());
        assertEquals(1, questions.getResults().size());
    }

    @Test
    public void findTheCorrectResultsWhenUsingFirstElementAndLimit() {
        final PartialResult<Question> questions = repo.getQuestions(new SearchParameter(10, 2), Optional.<Integer>absent());
        assertEquals(1, questions.getResults().size());
        assertEquals((Integer) 1, questions.getResults().get(0).id);
    }

    @Test
    public void getTheMostRecentQuestion() {
        Integer mostRecentQuestionFrom = repo.getMostRecentQuestionFrom(1);
        assertEquals((Integer) 32, mostRecentQuestionFrom);
    }

    @Test
    public void returnsTheUnansweredQuestion(){
        List<Integer> unansweredQuestionsFrom = repo.getUnansweredQuestionsFrom(1);
        assertEquals(1, unansweredQuestionsFrom.size());
        assertEquals(Integer.valueOf(32), unansweredQuestionsFrom.get(0));
    }


    @Test
    public void insertNewQuestion() {
        Question question = new Question(new Assembly(1, "Parlement Wallon", Assembly.Level.REGION), "test", "new question");
        question.year = 2014;
        question.dateAsked = new LocalDate();
        question.questionText = "Question text";
        repo.insertOrUpdateQuestion(question);
        PartialResult<Question> questionInserted = repo.getQuestions(new SearchParameter(50, null), Optional.<Integer>absent(), "new question");
        assertEquals(1, questionInserted.getResults().size());
        final Question actual = questionInserted.getResults().get(0);
        assertEquals("New question", actual.title);
        assertNotNull(actual.createdAt);
        assertNull(actual.updatedAt);
        final Integer id = actual.id;
        actual.title = "updated title";
        repo.insertOrUpdateQuestion(actual);
        final Question updatedQuestion = repo.getQuestionById(id);
        assertEquals("Updated title", updatedQuestion.title);
        assertNotNull(updatedQuestion.updatedAt);
    }
}
