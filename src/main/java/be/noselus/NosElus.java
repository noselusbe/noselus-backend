package be.noselus;

import be.noselus.repository.DeputyRepository;
import be.noselus.repository.DeputyRepositoryInMemory;
import be.noselus.repository.QuestionRepository;
import be.noselus.repository.QuestionRepositoryStub;
import be.noselus.service.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.setPort;

public class NosElus {

    public static QuestionRepository questionRepository = new QuestionRepositoryStub();
    public static DeputyRepository deputyRepository = new DeputyRepositoryInMemory();

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

        get(new JsonTransformer("/politicians", "politicians") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                return deputyRepository.getDeputies();
            }
        });

        get(new JsonTransformer("/politicians/:id", "politician") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return deputyRepository.getDeputyById(Integer.parseInt(params));
            }
        });

    }
}
