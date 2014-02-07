package be.noselus.job;

import be.noselus.scraping.ParliamentImporter;
import org.nnsoft.guice.guartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Scheduled(jobName = "updateWalloonParliamentQuestions", cronExpression = "* * 0/1 * * ?")
public class WalloonParliamentJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalloonParliamentJob.class);
    public static final int NBR_TO_EXTRACT = 1500;

    private final ParliamentImporter importer;
    private int fromId = 23416;// first question of current legislature

    @Inject
    public WalloonParliamentJob(final ParliamentImporter importer) {
        this.importer = importer;
    }

    @Override
    public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final int toId = fromId + NBR_TO_EXTRACT;
        LOGGER.debug("Launching importation for walloon parliament questions");
        importer.importQuestions(fromId, toId);
        fromId += NBR_TO_EXTRACT;
    }
}
