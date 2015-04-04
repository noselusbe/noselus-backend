package be.noselus.job;

import org.nnsoft.guice.guartz.QuartzModule;

public class NosElusQuartzModule extends QuartzModule {

    @Override
    protected void schedule() {
        scheduleJob(WalloonNewQuestionImporterJob.class);
        scheduleJob(WalloonNewResponseImporterJob.class);
        scheduleJob(QuestionIndexerJob.class);
        scheduleJob(WalloonParliamentRepresentativesImporterJob.class);
        configureScheduler().withManualStart();
    }
}
