package be.noselus;

import be.noselus.db.DatabaseUpdater;
import be.noselus.job.NosElusQuartzModule;
import be.noselus.pictures.PictureManager;
import be.noselus.service.Routes;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
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

        LOGGER.info("End initialization");
    }
}
