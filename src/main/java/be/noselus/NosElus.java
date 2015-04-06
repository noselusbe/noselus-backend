package be.noselus;

import be.noselus.db.DatabaseUpdater;
import be.noselus.job.NosElusQuartzModule;
import be.noselus.pictures.PictureManager;
import be.noselus.repository.QuestionRepository;
import be.noselus.repository.QuestionRepositoryInDatabase;
import be.noselus.search.SolrModule;
import be.noselus.service.Filters;
import be.noselus.service.Routes;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.palominolabs.metrics.guice.InstrumentationModule;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

/**
 * Entry point of the application.
 * Initialization and definition of the routes.
 */
public class NosElus {

    private static final Logger LOGGER = LoggerFactory.getLogger(NosElus.class);
    private static final String ENABLE_SOLR = "solr.enable";

    private final Set<Routes> routes;
    private final PictureManager pictureManager;
    private final DatabaseUpdater dbUpdater;
    private final Scheduler scheduler;
    private final MetricRegistry metricRegistry;
    private final Filters filters;

    @Inject
    public NosElus(final Set<Routes> routes, final PictureManager pictureManager, final DatabaseUpdater dbUpdater,
                   final Scheduler scheduler, final MetricRegistry metricRegistry, final Filters filters) {
        this.routes = routes;
        this.pictureManager = pictureManager;
        this.dbUpdater = dbUpdater;
        this.scheduler = scheduler;
        this.metricRegistry = metricRegistry;
        this.filters = filters;
    }

    public static void main(String[] args) throws IOException {
        LOGGER.debug("Starting up");
        List<Module> applicationModules = Lists.<Module>newArrayList(new NosElusModule(),
                new NosElusQuartzModule(),
                new InstrumentationModule());
        DynamicBooleanProperty withSolr =
                DynamicPropertyFactory.getInstance().getBooleanProperty(ENABLE_SOLR, true);
        if (withSolr.get()){
            applicationModules.add(new SolrModule());
        } else {
            applicationModules.add(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(QuestionRepository.class).to(QuestionRepositoryInDatabase.class);
                }
            });
        }

        Injector injector = Guice.createInjector(applicationModules);
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
            port(Integer.parseInt(port));
        }
        staticFileLocation("/public");

        for (Routes route : routes) {
            route.setup();
        }

        filters.cacheFilter();

        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                .outputTo(LoggerFactory.getLogger("be.noselus.perf"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(60, TimeUnit.MINUTES);

        LOGGER.info("End initialization");
    }
}
