package be.noselus.service;

import be.noselus.model.Question;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.QuestionRepository;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;

public class QuestionRoutesTest extends AbstractRoutesTest {

    private QuestionRepository questionRepository = mock(QuestionRepository.class);
    private PoliticianRepository politicianRepository = mock(PoliticianRepository.class);

    @Before
    public void setup() {
        QuestionRoutes routes = new QuestionRoutes(questionRepository, politicianRepository, new RoutesHelper());
        routes.setup();
        final Question question = new Question();
        question.id = 1;
        question.title = "question title";
        given(questionRepository.getQuestionById(anyInt())).willReturn(question);
    }


    @Test
    public void returnsExistingQuestion() {
        expect().statusCode(200).
                root("question").
                body("id", equalTo(1),
                        "title", equalTo("question title")).
                when().
                get("/questions/1");
    }
}
