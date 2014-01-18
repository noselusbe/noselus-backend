package be.noselus;

import be.noselus.db.DatabaseHelper;
import be.noselus.db.DatabaseUpdater;
import be.noselus.db.DbConfig;
import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.PersonSmall;
import be.noselus.pictures.PictureManager;
import be.noselus.repository.PoliticianRepository;
import be.noselus.repository.QuestionRepository;
import be.noselus.service.JsonTransformer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * Entry point of the application.
 * Initialization and definition of the routes.
 */
public class NosElus {

    private static final Logger LOGGER = LoggerFactory.getLogger(NosElus.class);

    public static final int NOT_FOUND = 404;
    public static final int DEFAULT_RESULT_LIMIT = 50;
    private final QuestionRepository questionRepository;
    private final PoliticianRepository politicianRepository;
    private final PictureManager pictureManager;
    private final DatabaseUpdater dbUpdater;
    private final DatabaseHelper dbHelper;
    private final DbConfig dbConfig;

    @Inject
    public NosElus(final QuestionRepository questionRepository, final PoliticianRepository politicianRepository, final PictureManager pictureManager, final DatabaseUpdater dbUpdater, final DatabaseHelper dbHelper, final DbConfig dbConfig) {
        this.questionRepository = questionRepository;
        this.politicianRepository = politicianRepository;
        this.pictureManager = pictureManager;
        this.dbUpdater = dbUpdater;
        this.dbHelper = dbHelper;
        this.dbConfig = dbConfig;
    }

    public static void main(String[] args) throws IOException {
        LOGGER.debug("Starting up");
        Injector injector = Guice.createInjector(new NosElusModule());
        final NosElus nosElus = injector.getInstance(NosElus.class);
        nosElus.initialize();
    }

    private void initialize() {
        LOGGER.info("Begin initialization");
        dbConfig.invoke();
        dbHelper.start();
        dbUpdater.update();
        pictureManager.start();

        final String port = System.getenv("PORT");
        if (port != null) {
            setPort(Integer.parseInt(port));
        }
        staticFileLocation("/public");

        questionRoutes();
        politicianRoutes();
        LOGGER.info("End initialization");
    }

    private void politicianRoutes() {
        get(new JsonTransformer("/politicians") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                return resultAs("politicians", politicianRepository.getPoliticians());
            }
        });

        get(new JsonTransformer("/politicians/:id") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return resultAs("politician", politicianRepository.getPoliticianById(Integer.parseInt(params)));
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
                        response.header("Cache-Control", "no-transform,public,max-age=300,s-maxage=900");
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
        get(new JsonTransformer("/questions") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                final String q = request.queryParams("q");
                final String askedBy = request.queryParams("asked_by");
                final SearchParameter parameter = getSearchParameter(request);
                if (q != null) {
                    final String keywords = q.replace("\"", "");
                    return resultAs("questions", questionRepository.searchByKeyword(parameter, keywords.split(" ")));
                } else if (askedBy != null) {
                    return resultAs("questions", questionRepository.questionAskedBy(parameter, Integer.valueOf(askedBy)));
                }
                return resultAs("questions", questionRepository.getQuestions(parameter));
            }
        });

        get(new JsonTransformer("/questions/:id") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":id");
                return resultAs("question", questionRepository.getQuestionById(Integer.parseInt(params)));
            }
        });

        get(new JsonTransformer("/questions/askedBy/:name") {
            @Override
            public Object myHandle(final Request request, final Response response) {
                final String params = request.params(":name");
                List<PersonSmall> list = politicianRepository.getPoliticianByName(params);
                if (list.isEmpty()) {
                    return null;
                } else {
                    return resultAs("questions", questionRepository.questionAskedBy(getSearchParameter(request), list.get(0).id));
                }

            }
        });
        get(new JsonTransformer("/questions/byEurovoc/:id") {

            @Override
            protected Object myHandle(Request request, Response response) {
                final String id = request.params(":id");
                return questionRepository.questionAssociatedToEurovoc(Integer.valueOf(id));
            }

        });
    }

    //Helpers

    private static SearchParameter getSearchParameter(final Request request) {
        final String firstElement = request.queryParams("first_element");
        final String limitAsked = request.queryParams("limit");
        final int limit;
        if (limitAsked == null) {
            limit = DEFAULT_RESULT_LIMIT;
        } else {
            limit = Integer.valueOf(limitAsked);
        }
        return new SearchParameter(limit, firstElement == null ? null : Integer.valueOf(firstElement));
    }

    private Map<String, Object> resultAs(final String key, final PartialResult<?> partialResult) {
        final Map<String, Object> result = new HashMap<>();
        result.put(key, partialResult.getResults());
        final Map<String, Object> meta = new HashMap<>();
        meta.put("next", partialResult.getNextItem());
        meta.put("limit", partialResult.getLimit());
        meta.put("total", partialResult.getTotalNumberOfResult());
        result.put("meta", meta);
        return result;
    }

    private Map<String, Object> resultAs(final String key, final Object object) {
        final Map<String, Object> result = new HashMap<>();
        result.put(key, object);
        return result;
    }
}
