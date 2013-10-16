package be.noselus.search;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class SolrHelper {
	
	private static SolrServer solrServer;
	
	public static SolrServer getSolrServer() {
		if (solrServer == null) {
			String solrUrl = System.getenv("SOLR_URL");
			
			if (solrUrl == null){
				// TODO add maven dependency solrServer = new EmbeddedSolrServer();
			} else {
				solrServer = new HttpSolrServer(solrUrl);
			}
			
			
		}
		
		return solrServer;
	}
	
	public static void add(SolrInputDocument doc, boolean commitNow) {
		try {
			SolrHelper.getSolrServer().add(doc);
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (commitNow == true) {
			try {
				SolrHelper.getSolrServer().commit();
			} catch (SolrServerException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void add(SolrInputDocument doc) {
		SolrHelper.add(doc, false);
	}

}
