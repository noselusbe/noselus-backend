package be.noselus.search;


import java.net.URI;
import java.util.Map;


public interface HasIndexableDocument {

    public enum type {
        WRITTEN_QUESTION
    }

    Map<SolrHelper.Fields, Object> getIndexableFields();

    URI getURI();

    type getType();

}
