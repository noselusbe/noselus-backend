package be.noselus.service;

import be.noselus.dto.PartialResult;
import be.noselus.dto.SearchParameter;
import spark.Request;
import spark.Route;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

@Singleton
public class RoutesHelper {

    public static final int DEFAULT_RESULT_LIMIT = 50;
    public static final int NOT_FOUND = 404;

    public SearchParameter getSearchParameter(final Request request) {
        final String firstElement = request.queryParams("first_element");
        final String limitAsked = request.queryParams("limit");
        final int limit;
        if (limitAsked == null) {
            limit = DEFAULT_RESULT_LIMIT;
        } else {
            limit = Integer.valueOf(limitAsked);
        }
        return new SearchParameter(limit, firstElement == null ? null : Integer.valueOf(firstElement));
    }

    public Map<String, Object> resultAs(final String key, final PartialResult<?> partialResult) {
        final Map<String, Object> result = new HashMap<>();
        result.put(key, partialResult.getResults());
        final Map<String, Object> meta = new HashMap<>();
        meta.put("next", partialResult.getNextItem());
        meta.put("limit", partialResult.getLimit());
        meta.put("total", partialResult.getTotalNumberOfResult());
        result.put("meta", meta);
        return result;
    }

    public Map<String, Object> resultAs(final String key, final Object object) {
        final Map<String, Object> result = new HashMap<>();
        result.put(key, object);
        return result;
    }

    public static void getJson(String path, Route route){
        get(path, "application/json", route, new JsonTransformer());
    }
}
