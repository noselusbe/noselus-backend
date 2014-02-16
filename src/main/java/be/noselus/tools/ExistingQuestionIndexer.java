package be.noselus.tools;


import be.noselus.NosElusModule;
import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Question;
import be.noselus.repository.QuestionRepositoryInDatabase;
import be.noselus.search.SolrHelper;
import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;


public class ExistingQuestionIndexer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistingQuestionIndexer.class);

    private final QuestionRepositoryInDatabase qr;
    private final SolrHelper solrHelper;

    @Inject
    public ExistingQuestionIndexer(final QuestionRepositoryInDatabase qr, final SolrHelper solrHelper) {
        this.qr = qr;
        this.solrHelper = solrHelper;
    }

    public static void main(String[] args) throws SolrServerException, IOException {
        Injector injector = Guice.createInjector(new NosElusModule());
        ExistingQuestionIndexer indexer = injector.getInstance(ExistingQuestionIndexer.class);
        indexer.indexQuestionsFromDatabase(100);
    }

    public void indexQuestionsFromDatabase(final int limit) {
        LOGGER.debug("Indexing with commit every {} question", limit);

        boolean continueIndexing = true;
        Integer firstElement = null;

        while (continueIndexing) {
            LOGGER.debug("First element: {}", firstElement);
            SearchParameter searchParameter = new SearchParameter(limit, firstElement);
            PartialResult<Question> questions = qr.getQuestions(searchParameter, Optional.<Integer>absent());
            LOGGER.debug("Next item : {} more: {}", questions.getNextItem(), questions.moreResultsAvailable());
            indexQuestions(questions.getResults());
            continueIndexing = questions.moreResultsAvailable();
            firstElement = (Integer) questions.getNextItem();
            solrHelper.commit();
        }
    }

    private void indexQuestions(final List<Question> questions) {
        for (final Question question : questions) {
            LOGGER.trace("Indexing {} - {}", question.id.toString(), question.title);
            solrHelper.add(question, false);
        }
    }

}
