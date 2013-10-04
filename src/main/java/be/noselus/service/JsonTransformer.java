package be.noselus.service;

import com.google.gson.Gson;
import spark.ResponseTransformerRoute;

public abstract class JsonTransformer extends ResponseTransformerRoute {

    private Gson gson = new Gson();

    protected JsonTransformer(String path) {
        super(path, "application/json");
    }

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
