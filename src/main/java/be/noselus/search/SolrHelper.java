package be.noselus.search;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class SolrHelper {
	
	private static SolrServer solrServer;
	
	public static SolrServer getSolrServer() {
		if (solrServer == null) {
			String solrUrl = System.getenv("SOLR_URL");
			solrServer = new HttpSolrServer(solrUrl);
		}
		
		return solrServer;
	}

}
