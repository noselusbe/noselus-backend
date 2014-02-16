package be.noselus.search;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;

@Singleton
public class SolrHelper {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T00:00:00Z'";

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrHelper.class);

    private final SolrServer solrServer;

    @Inject
    public SolrHelper(final SolrServer solrServer) {
        this.solrServer = solrServer;
    }


    public interface Fields {

        void index(Object value, SolrInputDocument indexableDoc);
    }

    public enum DateFields implements Fields {
        DATE_ASKED, DATE_ANSWERED;

        @Override
        public void index(final Object value, final SolrInputDocument indexableDoc) {
            LocalDate date = (LocalDate) value;
            indexableDoc.addField(this.name().toLowerCase(),
                    date.toString(DATE_FORMAT));
        }
    }

    public enum StringFields implements Fields {
        TITLE_FR, QUESTION_FR, ANSWER_FR, ASSEMBLY;

        @Override
        public void index(final Object value, final SolrInputDocument indexableDoc) {
            String field = (String) value;
            indexableDoc.addField(this.name().toLowerCase(),
                    field);
        }

    }

    public enum IntegerFields implements Fields {
        ASKED_BY_ID;

        @Override
        public void index(final Object value, final SolrInputDocument indexableDoc) {
            Integer field = (Integer) value;
            indexableDoc.addField(this.name().toLowerCase(), field);
        }
    }

    public QueryResponse query(final SolrParams params) {
        try {
            return solrServer.query(params);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        }
    }

    public UpdateResponse commit() {
        try {
            return solrServer.commit();
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(HasIndexableDocument doc, boolean commitNow) {

        SolrInputDocument indexableDoc = new SolrInputDocument();

        if (!doc.getIndexableFields().isEmpty()) {

            indexableDoc.addField("id", doc.getURI().toASCIIString());
            indexableDoc.addField("type", doc.getType());

            for (Map.Entry<Fields, Object> entry : doc.getIndexableFields().entrySet()) {
                entry.getKey().index(entry.getValue(), indexableDoc);
            }

            try {
                solrServer.add(indexableDoc);

            } catch (SolrServerException | IOException e) {
                LOGGER.error("Error adding document {}", indexableDoc.getFieldValue("id"), e);
            }

            if (commitNow == true) {
                try {
                    solrServer.commit();
                } catch (SolrServerException | IOException e) {
                    LOGGER.error("Error during commit ", e);
                }
            }
        }
    }

}
