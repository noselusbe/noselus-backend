package be.noselus.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.LocalDate;

public class SolrHelper {
	
	private static SolrServer solrServer;
	
	public interface Fields{}
	
	public enum DateFields implements Fields {
		DATE_ASKED, DATE_ANSWERED
	}
	
	public enum StringFields implements Fields {
		TITLE, QUESTION_FR, ANSWER_FR
	}
	
	public static final String DATE_FORMAT = "yyyy-mm-dd";
	
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
	
	public static void add(HasIndexableDocument doc, boolean commitNow) {
		
		SolrInputDocument indexableDoc = new SolrInputDocument();
		
		for (Map.Entry<Fields, Object> entry : doc.getIndexableFields().entrySet()) {
			if (entry.getKey() instanceof DateFields) {
				LocalDate date = (LocalDate) entry.getValue();
				indexableDoc.addField(String.valueOf(entry.getKey()), 
						date.toString(DATE_FORMAT));
				
			} else if (entry.getKey() instanceof StringFields) {
				String field = (String) entry.getValue();
				indexableDoc.addField(String.valueOf(entry.getKey()), 
						field);
				
			}
		}
		
		
		try {
			SolrHelper.getSolrServer().add(indexableDoc);
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
	
//	public static void add(SolrInputDocument doc) {
//		SolrHelper.add(doc, false);
//	}

}
