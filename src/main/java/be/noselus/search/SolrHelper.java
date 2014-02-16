package be.noselus.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.joda.time.LocalDate;


public class SolrHelper {
	
	private static SolrServer solrServer;
	

	public interface Fields{}
	
	public enum DateFields implements Fields {
		DATE_ASKED, DATE_ANSWERED
	}
	
	public enum StringFields implements Fields {
		TITLE_FR, QUESTION_FR, ANSWER_FR, ASSEMBLY
	}
	
	public static final String DATE_FORMAT = "yyyy-MM-dd'T00:00:00Z'";
	

	public static SolrServer getSolrServer() {
		if (solrServer == null) {
			String solrUrl = System.getenv("SOLR_URL");
			
			if (solrUrl == null){

				/*System.setProperty("solr.solr.home", "/home/shalinsmangar/work/oss/branch-1.3/example/solr");
				CoreContainer.Initializer initializer = new CoreContainer.Initializer();
				CoreContainer coreContainer = initializer.initialize();
				solrServer = new EmbeddedSolrServer(coreContainer, "");*/

			} else {
				solrServer = new HttpSolrServer(solrUrl);
			}
			
			
		}
		
		return solrServer;
	}
	
	public static void add(HasIndexableDocument doc, boolean commitNow) {
		
		SolrInputDocument indexableDoc = new SolrInputDocument();
		
		if (!doc.getIndexableFields().isEmpty()) {
			
			indexableDoc.addField("id", doc.getURI().toASCIIString());
			indexableDoc.addField("type", doc.getType());
			
			for (Map.Entry<Fields, Object> entry : doc.getIndexableFields().entrySet()) {
				if (entry.getKey() instanceof DateFields) {
					LocalDate date = (LocalDate) entry.getValue();
					indexableDoc.addField(String.valueOf(entry.getKey()).toLowerCase(), 
							date.toString(DATE_FORMAT));
					
				} else if (entry.getKey() instanceof StringFields) {
					String field = (String) entry.getValue();
					indexableDoc.addField(String.valueOf(entry.getKey()).toLowerCase(), 
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
	}
	
//	public static void add(SolrInputDocument doc) {
//		SolrHelper.add(doc, false);
//	}


}
