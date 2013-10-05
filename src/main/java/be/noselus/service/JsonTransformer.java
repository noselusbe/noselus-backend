package be.noselus.service;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.ResponseTransformerRoute;

import java.util.HashMap;
import java.util.Map;

public abstract class JsonTransformer extends ResponseTransformerRoute {

    private String rootKey;
    private Gson gson = new Gson();

    protected JsonTransformer(String path,  String rootKey) {
        super(path, "application/json");
        this.rootKey = rootKey;
    }



    @Override
    public String render(Object model) {
        Object actualObject;
        if (rootKey == null){
            actualObject = model;
        } else {
            Map<String,Object> objectWithRoot = new HashMap<>();
            objectWithRoot.put(rootKey, model);
            actualObject = objectWithRoot;
        }
        return gson.toJson(actualObject);
    }


    public Object handle(Request request, Response response){
        response.type("application/json");
        response.header( "Access-Control-Allow-Origin", "*");
        return myHandle( request,  response);
    }

    protected abstract Object myHandle(final Request request, final Response response);


}
