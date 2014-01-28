package be.noselus.service;

import be.noselus.NosElusTestModule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.jayway.restassured.RestAssured.expect;
import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.hamcrest.Matchers.equalTo;

public class QuestionRoutesTest extends AbstractRoutesTest {

    @Before
    public void setup(){
        Operation operation =
                sequenceOf(
                        deleteAllFrom("WRITTEN_QUESTION"),
                        insertInto("WRITTEN_QUESTION")
                                .columns("ID", "TITLE", "DATE_ASKED", "ASSEMBLY_ID", "QUESTION_TEXT")
                                .values(1L, "question title", new Date(), 1, "Question text")
                                .build());

        DbSetup dbSetup = new DbSetup(new DriverManagerDestination(NosElusTestModule.TEST_DB, null, null), operation);
        dbSetup.launch();
    }

    @Test
    public void returnsExistingQuestion() {
        expect().statusCode(200).
                root("question").
                body("id", equalTo(1),
                     "title", equalTo("Question title")).
                when().
                get("/questions/1");
    }
}
