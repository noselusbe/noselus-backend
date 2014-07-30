package be.noselus.repository;

import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Question;
import be.noselus.search.SolrHelper;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class QuestionRepositoryWithSolr extends QuestionRepositoryInDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionRepositoryWithSolr.class);

    private final SolrHelper solrHelper;


    @Inject
    public QuestionRepositoryWithSolr(final AssemblyRepository assemblyRepository, final DataSource dataSource, final SolrHelper solrHelper) {
        super(assemblyRepository, dataSource);
        this.solrHelper = solrHelper;
    }

    @Override
    @Timed
    public PartialResult<Question> getQuestions(final SearchParameter parameter, final Optional<Integer> askedById, final String... keywords) {
        final int limit = parameter.getLimit();
        final int offset = parameter.getFirstElement() == null ? 0 : (Integer) parameter.getFirstElement();

        SolrQuery parameters = solrHelper.buildSolrQuery(askedById, limit, offset, keywords);

        QueryResponse resp = solrHelper.query(parameters);

        List<Integer> questionIds = new ArrayList<>();
        for (SolrDocument solrDocument : resp.getResults()) {
            String uriString = (String) solrDocument.getFieldValue("id");
            String[] uriDecomposed = uriString.split(":");
            Integer id = Integer.valueOf(uriDecomposed[uriDecomposed.length - 1]);
            questionIds.add(id);
        }
        LOGGER.debug("Solr found questions with ids: {}, for the keywords: {}", Joiner.on(",").join(questionIds), Joiner.on(",").join(keywords));

        final List<Question> questionsFound = getQuestionsByIds(questionIds);
        final long numFound = resp.getResults().getNumFound();
        final Integer nextItem = offset + limit > numFound ? null : offset + limit;
        return new PartialResult<>(questionsFound, nextItem, limit, numFound);
    }
}
