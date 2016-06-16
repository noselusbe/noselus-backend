package be.noselus.scraping;

import be.noselus.NosElusModule;
import be.noselus.model.Question;
import be.noselus.repository.QuestionRepository;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ParliamentImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParliamentImporter.class);

    public static final int WAIT_INTERVAL = 450;
    public static final int FROM_ID = 53250;
    public static final int TO_ID = 53271;
    public static final String QUESTION_URL = "https://www.parlement-wallonie.be/content/print.php?print=interp-questions-voir.php&type=all&id_doc=";

    @Inject
    public ParliamentImporter(final QuestionRepository questionRepository, final QuestionParser parser) {
        this.questionRepository = questionRepository;
        this.parser = parser;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        Injector injector = Guice.createInjector(new NosElusModule());
        ParliamentImporter instance = injector.getInstance(ParliamentImporter.class);
        instance.updateRepository();
    }

    private final QuestionRepository questionRepository;
    private final QuestionParser parser;

    private void updateRepository() {
        importQuestions(FROM_ID, TO_ID);
    }

    public void importQuestions(int fromId, int toId) {
        LOGGER.debug("Importing questions from " + fromId + " to " + toId);
        for (int id = fromId; id < toId; id++) {
            importQuestion(id);
        }

    }

    public void importQuestions(List<Integer> questionIds){
        for (Integer questionId : questionIds) {
            importQuestion(questionId);
        }
    }

    private void importQuestion( final int id) {
        try {
            LOGGER.debug("importing question " + id);
            final Question parsedQuestion = parser.parse(id);
            if (parsedQuestion != null) {
                questionRepository.insertOrUpdateQuestion(parsedQuestion);
            }
        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException e) {
            LOGGER.error(QUESTION_URL + id, e);
        }
        waitInterval();
    }

    private void waitInterval() {
        try {
            Thread.sleep(WAIT_INTERVAL);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
