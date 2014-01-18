package be.noselus.repository;

import be.noselus.AbstractDbDependantTest;
import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Question;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuestionRepositoryTest extends AbstractDbDependantTest {

    private QuestionRepository repo;

    @Before
    public void setUp() {
        AssemblyRegistry assemblies = new AssemblyRegistryInDatabase(AbstractDbDependantTest.dbHelper);
        Operation operation =
                sequenceOf(
                        deleteAllFrom("WRITTEN_QUESTION"),
                        insertInto("WRITTEN_QUESTION")
                                .columns("ID", "TITLE", "DATE_ASKED", "ASSEMBLY_ID", "QUESTION_TEXT")
                                .values(1L, "Flandre", new Date(), 1, "Question text")
                                .values(2L, "Wallonie", new Date(), 1, "Question text")
                                .values(536L, "Question 536", new Date(), 1, "Question text")
                                .build());

        DbSetup dbSetup = new DbSetup(new DriverManagerDestination("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", null, null), operation);
        dbSetup.launch();
        repo = new QuestionRepositoryInDatabase(assemblies, AbstractDbDependantTest.dbHelper);
    }

    @Test
    public void findByKeyWord() {
        final List<Question> questions = repo.searchByKeyword(new SearchParameter(), "Flandre", "Wallonie").getResults();
        assertTrue(!questions.isEmpty());
    }

    @Test
    public void findWithLimitReturnNoMoreThanLimit() {
        final PartialResult<Question> questions = repo.getQuestions(new SearchParameter(1));
        assertEquals(1, questions.getResults().size());
    }

    @Test
    public void findTheRightResult() {
        final PartialResult<Question> questions = repo.getQuestions(new SearchParameter(10, 536));
        assertEquals(3, questions.getResults().size());
        for (Question question : questions.getResults()) {
            assertTrue(question.id < 537);
        }
    }
}
