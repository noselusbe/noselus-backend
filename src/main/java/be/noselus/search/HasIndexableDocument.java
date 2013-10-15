package be.noselus.search;

import org.apache.solr.common.SolrInputDocument;

public interface HasIndexableDocument {
	
	public static String TYPE = "type";
	
	public static final String WRITTEN_QUESTION = "WRITTEN_QUESTION";

	public SolrInputDocument getIndexableDocument();
}
