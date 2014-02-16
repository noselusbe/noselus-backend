package be.noselus;

import be.noselus.db.DatabaseUpdater;
import be.noselus.db.DbConfig;
import be.noselus.repository.*;
import be.noselus.search.SolrHelper;
import be.noselus.service.PoliticianRoutes;
import be.noselus.service.QuestionRoutes;
import be.noselus.service.Routes;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import javax.inject.Singleton;
import javax.sql.DataSource;

public class NosElusModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AssemblyRegistry.class).to(AssemblyRegistryInDatabase.class);
        bind(PoliticianRepository.class).to(PoliticianRepositoryInDatabase.class);
//        bind(QuestionRepository.class).to(QuestionRepositoryInDatabase.class);
        bind(QuestionRepository.class).to(QuestionRepositoryWithSolr.class);
        Multibinder<Routes> routesMultibinder = Multibinder.newSetBinder(binder(), Routes.class);
        routesMultibinder.addBinding().to(QuestionRoutes.class);
        routesMultibinder.addBinding().to(PoliticianRoutes.class);
        requireBinding(DataSource.class);
        requireBinding(DbConfig.class);
        requireBinding(DatabaseUpdater.class);
        requireBinding(SolrHelper.class);
    }

    @Provides
    @Singleton
    DbConfig getDbConfig() {
        return new DbConfig().invoke();
    }

    @Provides
    @Singleton
    DataSource getDataSource(DbConfig config) {
        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setDriverClass(config.getDriver());
        ds.setJdbcUrl(config.getUrl());
        ds.setUsername(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setMinConnectionsPerPartition(5);
        ds.setMaxConnectionsPerPartition(18);
        ds.setPartitionCount(1);
        return ds;
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
