package be.noselus.job;

import be.noselus.fix.PersonWithMissingAssemblyRefFix;
import be.noselus.fix.QuestionWithoutPersonAskingFix;
import be.noselus.scraping.WalloonRepresentativesCsvImporter;
import org.nnsoft.guice.guartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Scheduled(jobName = "loadWalloonParliamentRepresentatives", cronExpression = "0 20 12 1/1 * ? *")
public class WalloonParliamentRepresentativesImporterJob implements Job {

    private final WalloonRepresentativesCsvImporter walRepCsvImporter;
    private final QuestionWithoutPersonAskingFix questionWithoutPersonAskingFix;
    private final PersonWithMissingAssemblyRefFix personFix;

    @Inject
    public WalloonParliamentRepresentativesImporterJob(final WalloonRepresentativesCsvImporter walRepCsvImporter,
                                                       final QuestionWithoutPersonAskingFix questionWithoutPersonAskingFix,
                                                       final PersonWithMissingAssemblyRefFix personFix) {
        this.walRepCsvImporter = walRepCsvImporter;
        this.questionWithoutPersonAskingFix = questionWithoutPersonAskingFix;
        this.personFix = personFix;
    }

    @Override
    public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        walRepCsvImporter.importLatest();
//        questionWithoutPersonAskingFix.runFix();
        personFix.runFix();
    }
}
