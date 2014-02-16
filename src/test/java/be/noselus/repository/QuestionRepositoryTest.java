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

import java.util.Date;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.junit.Assert.*;

public class QuestionRepositoryTest extends AbstractDbDependantTest {

    private QuestionRepository repo;

    @Before
    public void setUp() {
        AssemblyRegistry assemblies = new AssemblyRegistryInDatabase(AbstractDbDependantTest.dataSource);
        Operation operation =
                sequenceOf(
                        deleteAllFrom("WRITTEN_QUESTION"),
                        insertInto("WRITTEN_QUESTION")
                                .columns("ID", "TITLE", "DATE_ASKED", "ASSEMBLY_ID", "QUESTION_TEXT")
                                .values(1L, "Flandre", new Date(), 1, "Question text")
                                .values(2L, "Wallonie", new Date(), 1, "Question text")
                                .values(536L, "Question 536", new Date(), 1, "Question text")
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
    public void findTheRightResult() {
        final PartialResult<Question> questions = repo.getQuestions(new SearchParameter(10, 2), Optional.<Integer>absent());
        assertEquals(1, questions.getResults().size());
        assertEquals((Integer)1, questions.getResults().get(0).id);
    }

    @Test
    public void insertNewQuestion() {
        Question question = new Question();
        question.year = 2014;
        question.title = "new question";
        question.dateAsked = new LocalDate();
        question.assembly = new Assembly(1, "Parlement Wallon", Assembly.Level.REGION);
        question.questionText = "Question text";
        question.assemblyRef = "test";
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
