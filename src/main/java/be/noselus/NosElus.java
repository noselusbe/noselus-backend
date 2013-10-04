package be.noselus;

import static spark.Spark.get;

import java.io.IOException;

import spark.Request;
import spark.Response;
import spark.Route;
import be.noselus.repository.QuestionRepository;
import be.noselus.repository.QuestionRepositoryStub;
import be.noselus.service.JsonTransformer;

public class NosElus {

    public static QuestionRepository questionRepository = new QuestionRepositoryStub();

    public static void main(String[] args) throws IOException {

        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "<html><head></head><body>Hello World!</body></html>";
            }
        });

        get(new JsonTransformer("/questions") {

            @Override
            public Object handle(final Request request, final Response response) {
                return questionRepository.getQuestions();
            }
        });

    }
}
