package be.noselus.job;

import be.noselus.NosElusModule;
import be.noselus.repository.QuestionRepository;
import be.noselus.scraping.ParliamentImporter;
import be.noselus.search.SolrModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import org.nnsoft.guice.guartz.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Scheduled(jobName = "walloonNewQuestionImporter", cronExpression = "0 0 1 1/1 * ?")
public class WalloonNewQuestionImporterJob implements Job {

    public static final int WALLOON_PARLIAMENT_ID = 1;
    public static final int DEFAULT_NBR_QUESTION_TO_IMPORT = 500;
    private final ParliamentImporter importer;
    private final QuestionRepository questionRepository;
    public static final String NBR_QUESTION_TO_IMPORT = "walloon.question.to.import";

    @Inject
    public WalloonNewQuestionImporterJob(final ParliamentImporter importer, final QuestionRepository questionRepository) {
        this.importer = importer;
        this.questionRepository = questionRepository;
    }

    public static void main(String[] args) throws JobExecutionException {
        Injector injector = Guice.createInjector(new NosElusModule(), new SolrModule()  );
        final WalloonNewQuestionImporterJob instance = injector.getInstance(WalloonNewQuestionImporterJob.class);
        instance.execute(null);
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        DynamicIntProperty nbrDocumentToScan =
                DynamicPropertyFactory.getInstance().getIntProperty(NBR_QUESTION_TO_IMPORT, DEFAULT_NBR_QUESTION_TO_IMPORT);
        final Integer lastQuestion = questionRepository.getMostRecentQuestionFrom(WALLOON_PARLIAMENT_ID);
        importer.importQuestions(lastQuestion, lastQuestion + nbrDocumentToScan.get());
    }
}
