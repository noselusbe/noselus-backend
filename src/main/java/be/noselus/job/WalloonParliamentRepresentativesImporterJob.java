package be.noselus.job;

import be.noselus.fix.QuestionWithoutPersonAskingFix;
import be.noselus.scraping.WalloonRepresentativesCsvImporter;
import org.nnsoft.guice.guartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Scheduled(jobName = "loadWalloonParliamentRepresentatives", cronExpression = "0 0 1 ? * SUN *")
public class WalloonParliamentRepresentativesImporterJob implements Job {

    private final WalloonRepresentativesCsvImporter walRepCsvImporter;
    private final QuestionWithoutPersonAskingFix questionWithoutPersonAskingFix;

    @Inject
    public WalloonParliamentRepresentativesImporterJob(final WalloonRepresentativesCsvImporter walRepCsvImporter,
                                                       final QuestionWithoutPersonAskingFix questionWithoutPersonAskingFix) {
        this.walRepCsvImporter = walRepCsvImporter;
        this.questionWithoutPersonAskingFix = questionWithoutPersonAskingFix;
    }

    @Override
    public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        walRepCsvImporter.importLatest();
        questionWithoutPersonAskingFix.runFix();
    }
}
