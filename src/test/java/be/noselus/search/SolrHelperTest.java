package be.noselus.search;

import com.google.common.base.Optional;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SolrHelperTest {

    private SolrHelper solrHelper;

    @Before
    public void createSolrHelper(){
        solrHelper = new SolrHelper(null);
    }

    @Test
    public void shouldEscapePlusSign(){
        SolrQuery actualQuery = solrHelper.buildSolrQuery(Optional.<Integer>absent(), 20, 0, "+construction");
        assertThat(actualQuery.get("q"), is("text:\\+construction"));
    }
}
