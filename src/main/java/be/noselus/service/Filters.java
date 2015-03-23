package be.noselus.service;

import static spark.Spark.before;

public class Filters {

    public void cacheFilter() {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Cache-Control", "no-transform,public,max-age=3600,s-maxage=4800");
        });
    }

    public void jsonFilter() {
        before((request, response) -> {
            response.type("application/json");
        });
    }
}
