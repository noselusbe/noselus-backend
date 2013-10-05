package be.noselus;

import be.noselus.repository.*;
import be.noselus.service.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.setPort;

public class NosElus {

    public static QuestionRepository questionRepository = new QuestionRepositoryInDatabase();
    public static PoliticianRepository politicianRepository = new PoliticianRepositoryInDatabase();

    public static void main(String[] args) throws IOException {
        final String port = System.getenv("PORT");
        if (port != null){
          setPort(Integer.parseInt(port));

        }

        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "<html><head></head><body>Hello World!</body></html>";
            }
        });

        get(new JsonTransformer("/questions", "questions") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                return questionRepository.getQuestions();
            }
        });

        get(new JsonTransformer("/questions/:id", "question") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return questionRepository.getQuestionById(Integer.parseInt(params));
            }
        });

        get(new JsonTransformer("/politicians", "politicians") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                return politicianRepository.getPoliticians();
            }
        });

        get(new JsonTransformer("/politicians/:id", "politician") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return politicianRepository.getPoliticianById(Integer.parseInt(params));
            }
        });

    }
}
