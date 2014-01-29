package be.noselus.service;

import be.noselus.NosElusTestModule;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.ResponseSpecification;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.jayway.restassured.RestAssured.expect;
import static com.ninja_squad.dbsetup.Operations.*;
import static org.hamcrest.Matchers.*;

public class QuestionRoutesTest extends AbstractRoutesTest {

    private ResponseSpecification responseSpec;

    @Before
    public void setup() {
        Operation operation =
                sequenceOf(
                        deleteAllFrom("WRITTEN_QUESTION"),
                        insertInto("WRITTEN_QUESTION")
                                .columns("ID", "TITLE", "DATE_ASKED", "ASSEMBLY_ID", "QUESTION_TEXT", "ASKED_BY", "ASKED_TO")
                                .values(1L, "question title", new Date(), 1, "Question text", 896, 151)
                                .values(2L, "question title", new Date(), 1, "Question text", 89, 151)
                                .values(3L, "the meaning of life", new Date(), 1, "Question text", 78, 155)
                                .build());

        DbSetup dbSetup = new DbSetup(new DriverManagerDestination(NosElusTestModule.TEST_DB, null, null), operation);
        dbSetup.launch();

        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(200);
        responseSpec = builder.build();

    }

    @Test
    public void returnsQuestionById() {
        expect().spec(responseSpec)
                .root("question")
                .body("id", equalTo(1),
                        "title", equalTo("Question title"))
                .when()
                .get("/questions/1");
    }

    @Test
    public void returnsAllQuestions() {
        expect().spec(responseSpec)
                .body("questions.size()", equalTo(3))
                .when()
                .get("/questions");
    }

    @Test
    public void returnsQuestionsWithKeywords() {
        expect().spec(responseSpec)
                .body("questions.size()", greaterThan(0),
                        "questions.title", not(hasItem(not(containsString("title")))))
                .when()
                .get("/questions?q=title");
    }

    @Test
    public void returnsOnlyQuestionsAskedBy() {
        expect().spec(responseSpec)
                .body("questions.askedBy", not(hasItem(not(89))))
                .when()
                .get("/questions?asked_by=89");
    }

    @Test
    public void returnsRightQuestionWhenAskedByAndKeywordCombined() {
        expect().spec(responseSpec)
                .body("questions.size()", greaterThan(0),
                        "questions.askedBy", not(hasItem(not(896))),
                        "questions.title", not(hasItem(not(containsString("title")))))
                .when()
                .get("/questions?q=title&asked_by=896");
    }

    @Test
    public void returnsQuestionAskedBy() {
        expect().spec(responseSpec)
                .body("questions.size()", greaterThan(0),
                        "questions.askedBy", not(hasItem(not(78)))
                )
                .when()
                .get("/questions/askedBy/BASTIN");
    }
}
