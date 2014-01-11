package be.noselus;

import be.noselus.db.DatabaseUpdater;
import be.noselus.model.PersonSmall;
import be.noselus.pictures.PictureManager;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.QuestionRepository;
import be.noselus.service.JsonTransformer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static spark.Spark.*;

/**
 * Entry point of the application.
 * Initialization and definition of the routes.
 */
public class NosElus {

    public static final int NOT_FOUND = 404;
    private final QuestionRepository questionRepository;
    private final PoliticianRepository politicianRepository;
    private final PictureManager pictureManager;
    private final DatabaseUpdater dbUpdater;

    @Inject
    public NosElus(final QuestionRepository questionRepository, final PoliticianRepository politicianRepository, final PictureManager pictureManager, final DatabaseUpdater dbUpdater) {
        this.questionRepository = questionRepository;
        this.politicianRepository = politicianRepository;
        this.pictureManager = pictureManager;
        this.dbUpdater = dbUpdater;
    }

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new NosElusModule());
        final NosElus nosElus = injector.getInstance(NosElus.class);
        nosElus.initialize();
    }

    private void initialize() {
        dbUpdater.update();
        final String port = System.getenv("PORT");
        if (port != null) {
            setPort(Integer.parseInt(port));
        }
        staticFileLocation("/public");

        questionRoutes();
        politicianRoutes();
    }

    private void politicianRoutes() {
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

        get(new Route("/politicians/picture/:id") {
            @Override
            public Object handle(final Request request, final Response response) {
                try {
                    final String id = request.params(":id");
                    byte[] out;
                    InputStream is = pictureManager.get(Integer.valueOf(id));

                    if (is == null) {
                        response.status(NOT_FOUND);
                        return null;
                    } else {
                        out = IOUtils.toByteArray(is);
                        response.raw().setContentType("image/jpeg;charset=utf-8");
                        response.raw().getOutputStream().write(out, 0, out.length);
                        return out;
                    }
                } catch (NumberFormatException | IOException e) {
                    response.status(NOT_FOUND);
                    return null;
                }
            }
        });

        get(new Route("/politicians/picture/:id/*/*") {
            @Override
            public Object handle(final Request request, final Response response) {
                try {
                    final String id = request.params(":id");
                    int width = Integer.valueOf(request.splat()[0]);
                    int height = Integer.valueOf(request.splat()[1]);

                    response.raw().setContentType("image/jpeg;charset=utf-8");
                    pictureManager.get(Integer.valueOf(id), width, height, response.raw().getOutputStream());
                    return null;
                } catch (NumberFormatException | IOException e) {
                    response.status(NOT_FOUND);
                    return null;
                }
            }
        });
    }

    private void questionRoutes() {
        get(new JsonTransformer("/questions", "questions") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                final String q = request.queryParams("q");
                final String askedBy = request.queryParams("asked_by");
                if (q != null) {
                    final String keywords = q.replace("\"", "");
                    return questionRepository.searchByKeyword(keywords.split(" "));
                } else if (askedBy != null) {
                    return questionRepository.questionAskedBy(Integer.valueOf(askedBy));
                }
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

        get(new JsonTransformer("/questions/askedBy/:name", "questions") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":name");
                List<PersonSmall> list = politicianRepository.getPoliticianByName(params);
                if (list.isEmpty()) {
                    return null;
                } else {
                    return questionRepository.questionAskedBy(list.get(0).id);
                }

            }
        });
        get(new JsonTransformer("/questions/byEurovoc/:id", "questions") {

            @Override
            protected Object myHandle(Request request, Response response) {
                final String id = request.params(":id");
                return questionRepository.questionAssociatedToEurovoc(Integer.valueOf(id));
            }

        });
    }
}
