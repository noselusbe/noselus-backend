package be.noselus.search;

import be.noselus.repository.QuestionRepository;
import be.noselus.repository.QuestionRepositoryWithSolr;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import javax.inject.Singleton;

public class SolrModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(QuestionRepository.class).to(QuestionRepositoryWithSolr.class);
        requireBinding(SolrHelper.class);
    }

    @Provides
    @Singleton
    SolrServer getSolrServer() {
        String solrUrl = System.getenv("SOLR_URL");
        if (solrUrl == null) {
            return null;
        }
        return new HttpSolrServer(solrUrl);
    }
}
