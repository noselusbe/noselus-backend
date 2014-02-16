package be.noselus.repository;

import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import be.noselus.model.Question;
import be.noselus.search.SolrHelper;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuestionRepositoryWithSolr extends QuestionRepositoryInDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionRepositoryWithSolr.class);

    private final SolrHelper solrHelper;


    @Inject
    public QuestionRepositoryWithSolr(final AssemblyRegistry assemblyRegistry, final DataSource dataSource, final SolrHelper solrHelper) {
        super(assemblyRegistry, dataSource);
        this.solrHelper = solrHelper;
    }

    @Override
    public PartialResult<Question> getQuestions(final SearchParameter parameter, final Optional<Integer> askedById, final String... keywords) {

        SolrQuery parameters = new SolrQuery();
        String query;
        if (keywords.length > 0) {
            query = "text:" + Joiner.on("+").join(keywords);
            if (askedById.isPresent()) {
                query += " AND asked_by_id:" + askedById.get();
            }
        } else {
            if (askedById.isPresent()) {
                query = "asked_by_id:" + askedById.get();
            } else {
                query = "*:*";
            }
        }

        parameters.set("q", query);
        final int limit = parameter.getLimit();
        parameters.set("rows", limit);
        final int offset = parameter.getFirstElement() == null ? 0 : (Integer) parameter.getFirstElement();
        parameters.set("start", offset);
        parameters.set("sort", "date_asked desc, id desc");

        QueryResponse resp = solrHelper.query(parameters);

        SolrDocumentList list = resp.getResults();
        List<Question> result = new ArrayList<>();

        Iterator<SolrDocument> i = list.iterator();

        //TODO get all the questions in one select instead of 1 query per question
        while (i.hasNext()) {
            String uriString = (String) i.next().getFieldValue("id");
            //until now, we just query into question table, with id field
            String[] uriDecomposed = uriString.split(":");
            Integer id = Integer.valueOf(uriDecomposed[uriDecomposed.length - 1]);
            result.add(getQuestionById(id));
        }
        final long numFound = resp.getResults().getNumFound();
        final Integer nextItem = offset + limit > numFound ? null : offset + limit;
        return new PartialResult<>(result, nextItem, limit, numFound);
    }
}
