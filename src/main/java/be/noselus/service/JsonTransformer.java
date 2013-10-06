package be.noselus.service;

import com.google.gson.*;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import spark.Request;
import spark.Response;
import spark.ResponseTransformerRoute;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonTransformer extends ResponseTransformerRoute {

    private String rootKey;
    private Gson gson;

    protected JsonTransformer(String path, String rootKey) {
        super(path, "application/json");
        this.rootKey = rootKey;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
    }

    @Override
    public String render(Object model) {
        Object actualObject;
        if (rootKey == null) {
            actualObject = model;
        } else {
            Map<String, Object> objectWithRoot = new HashMap<>();
            objectWithRoot.put(rootKey, model);
            actualObject = objectWithRoot;
        }
        return gson.toJson(actualObject);
    }


    public Object handle(Request request, Response response) {
        response.type("application/json");
        response.header("Access-Control-Allow-Origin", "*");
        return myHandle(request, response);
    }

    protected abstract Object myHandle(final Request request, final Response response);

    public final class LocalDateAdapter implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
        final org.joda.time.format.DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.date();

        @Override
        public LocalDate deserialize(final JsonElement je, final Type type,
                                     final JsonDeserializationContext jdc) throws JsonParseException {
            return je.getAsString().length() == 0 ? null : DATE_TIME_FORMATTER.parseLocalDate(je.getAsString());
        }

        @Override
        public JsonElement serialize(final LocalDate localDate, final Type type, final JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDate == null ? StringUtils.EMPTY : DATE_TIME_FORMATTER.print(localDate));
        }
    }

}
