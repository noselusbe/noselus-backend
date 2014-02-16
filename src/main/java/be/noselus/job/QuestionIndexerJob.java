package be.noselus.job;

import be.noselus.tools.ExistingQuestionIndexer;
import org.nnsoft.guice.guartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Scheduled(jobName = "questionIndexer", cronExpression = "0 0 3 1/1 * ?")
public class QuestionIndexerJob implements Job {

    private final ExistingQuestionIndexer indexer;

    @Inject
    public QuestionIndexerJob(final ExistingQuestionIndexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        indexer.indexQuestionsFromDatabase(100);
    }
}
