package be.noselus.job;

import be.noselus.NosElusModule;
import be.noselus.repository.QuestionRepository;
import be.noselus.scraping.ParliamentImporter;
import be.noselus.search.DbSearchModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.nnsoft.guice.guartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Scheduled(jobName = "walloonNewQuestionImporter", cronExpression = "0 30 0 1/1 * ?")
public class WalloonNewResponseImporterJob implements Job {

    private final QuestionRepository questionRepository;
    private final ParliamentImporter importer;

    @Inject
    public WalloonNewResponseImporterJob(final QuestionRepository questionRepository, final ParliamentImporter importer) {
        this.questionRepository = questionRepository;
        this.importer = importer;
    }

    public static void main(String[] args) throws JobExecutionException {
        Injector injector = Guice.createInjector(new NosElusModule(), new DbSearchModule());
        final WalloonNewResponseImporterJob instance = injector.getInstance(WalloonNewResponseImporterJob.class);
        instance.execute(null);
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        List<Integer> unAnsweredQuestionRefs = questionRepository.getUnansweredQuestionsFrom(1);
        importer.importQuestions(unAnsweredQuestionRefs);
    }
}
