package be.noselus.job;

import be.noselus.NosElusModule;
import be.noselus.repository.QuestionRepository;
import be.noselus.scraping.ParliamentImporter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.nnsoft.guice.guartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Scheduled(jobName = "walloonNewQuestionImporter", cronExpression = "0 0 1 1/1 * ?")
public class WalloonNewQuestionImporter implements Job {

    public static final int WALLOON_PARLIAMENT_ID = 1;
    private final ParliamentImporter importer;
    private final QuestionRepository questionRepository;
    public static final Integer NBR_QUESTION_TO_IMPORT = 100;

    @Inject
    public WalloonNewQuestionImporter(final ParliamentImporter importer, final QuestionRepository questionRepository) {
        this.importer = importer;
        this.questionRepository = questionRepository;
    }

    public static void main(String[] args) throws JobExecutionException {
        Injector injector = Guice.createInjector(new NosElusModule());
        final WalloonNewQuestionImporter instance = injector.getInstance(WalloonNewQuestionImporter.class);
        instance.execute(null);
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final Integer lastQuestion = questionRepository.getMostRecentQuestionFrom(WALLOON_PARLIAMENT_ID);
        importer.importQuestions(lastQuestion, lastQuestion + NBR_QUESTION_TO_IMPORT);
    }
}
