package be.noselus;

import be.noselus.model.Person;
import be.noselus.model.PersonSmall;
import be.noselus.pictures.PictureManager;
import be.noselus.service.Routes;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.Set;

import static spark.Spark.setPort;
import static spark.Spark.staticFileLocation;

/**
 * Entry point of the application.
 * Initialization and definition of the routes.
 */
public class NosElus {

    private static final Logger LOGGER = LoggerFactory.getLogger(NosElus.class);

    private final Set<Routes> routes;
    private final PictureManager pictureManager;
    private final DatabaseUpdater dbUpdater;
    private final Scheduler scheduler;

    @Inject
    public NosElus(final Set<Routes> routes, final PictureManager pictureManager, final DatabaseUpdater dbUpdater, final Scheduler scheduler) {
        this.routes = routes;
        this.pictureManager = pictureManager;
        this.dbUpdater = dbUpdater;
        this.scheduler = scheduler;
    }

    public static void main(String[] args) throws IOException {
        LOGGER.debug("Starting up");
        Injector injector = Guice.createInjector(new NosElusModule(), new NosElusQuartzModule());
        final NosElus nosElus = injector.getInstance(NosElus.class);
        nosElus.initialize();
        nosElus.startScheduler();
    }

    private void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        LOGGER.info("Begin initialization");
        dbUpdater.update();
        pictureManager.start();

        final String port = System.getenv("PORT");
        if (port != null) {
            setPort(Integer.parseInt(port));
        }
        staticFileLocation("/public");

        for (Routes route : routes) {
            route.setup();
        }

        get(new JsonTransformer("/questions", "questions") {

            @Override
            public Object myHandle(final Request request, final Response response) {
                final String q = request.queryParams("q");
                final String askedBy = request.queryParams("asked_by");
                if (q != null) {
                    final String keywords = q.replace("\"", "").replace(" ", "+");

                    SolrQuery parameters = new SolrQuery();
                    parameters.set("q", "text:" + keywords);

                    try {
                        QueryResponse resp = SolrHelper.getSolrServer().query(parameters);

                        SolrDocumentList list = resp.getResults();

                        List<Question> result = new ArrayList<Question>();

                        Iterator<SolrDocument> i = list.iterator();

                        while (i.hasNext()) {
                            String uriString = (String) i.next().getFieldValue("id");
                            //until now, we just query into question table, with id field
                            String[] uriDecomposed = uriString.split(":");
                            Integer id = Integer.valueOf(uriDecomposed[uriDecomposed.length - 1]);
                            result.add(questionRepository.getQuestionById(id));
                        }

                        return result;


                    } catch (SolrServerException e) {
                        e.printStackTrace();
                    }

                } else if (askedBy != null) {
                    return questionRepository.questionAskedBy(Integer.valueOf(askedBy));
                }
                return questionRepository.getQuestions(50, 0);
            }
        });
        
        LOGGER.info("End initialization");
    }
}
