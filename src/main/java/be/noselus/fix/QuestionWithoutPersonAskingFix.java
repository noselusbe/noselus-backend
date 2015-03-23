package be.noselus.fix;

import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Question;
import be.noselus.repository.QuestionRepositoryInDatabase;
import be.noselus.scraping.ParliamentImporter;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class QuestionWithoutPersonAskingFix {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionWithoutPersonAskingFix.class);

    private final QuestionRepositoryInDatabase questionRepository;
    private final ParliamentImporter importer;

    @Inject
    public QuestionWithoutPersonAskingFix(final QuestionRepositoryInDatabase questionRepository, final ParliamentImporter importer) {
        this.questionRepository = questionRepository;
        this.importer = importer;
    }

    public void runFix(){

        boolean continueSearching = true;
        Integer firstElement = null;

        while (continueSearching) {
            LOGGER.debug("First element: {}", firstElement);
            SearchParameter searchParameter = new SearchParameter(100, firstElement);
            PartialResult<Question> questions = questionRepository.getQuestions(searchParameter, Optional.of(0));
            LOGGER.debug("Next item : {} more: {}", questions.getNextItem(), questions.moreResultsAvailable());
            fixQuestions(questions.getResults());
            continueSearching = questions.moreResultsAvailable();
            firstElement = (Integer) questions.getNextItem();
        }
    }

    private void fixQuestions(final List<Question> results) {
        final List<Integer> questionIds = Lists.transform(results, input -> Integer.valueOf(input.assemblyRef));
        importer.importQuestions(questionIds);
    }
}
