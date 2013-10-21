package be.noselus.search;

import java.net.URI;
import java.util.Map;


public interface HasIndexableDocument {
	
	public enum type {
		WRITTEN_QUESTION
	}
		
	public static final String WRITTEN_QUESTION = "WRITTEN_QUESTION";

	public Map<SolrHelper.Fields, Object> getIndexableFields();
	public URI getURI();
	public type getType();
}
