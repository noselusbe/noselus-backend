package be.noselus.job;

import org.nnsoft.guice.guartz.QuartzModule;

public class NosElusQuartzModule extends QuartzModule {

    @Override
    protected void schedule() {
        scheduleJob(WalloonParliamentJob.class);
        configureScheduler().withManualStart();
    }
}
