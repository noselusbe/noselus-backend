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

/**
 * Response transformer route that is in charge of converting object response into Json response.
 */
public abstract class JsonTransformer extends ResponseTransformerRoute {

    private final String rootKey;
    private final Gson gson;

    protected JsonTransformer(String path, String rootKey) {
        super(path, "application/json");
        this.rootKey = rootKey;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
    }

    @Override
    public String render(Object model) {
        Map<String, Object> objectWithRoot = new HashMap<>();
        objectWithRoot.put(rootKey, model);
        return gson.toJson(objectWithRoot);
    }

    @Override
    public Object handle(Request request, Response response) {
        response.type("application/json");
        response.header("Access-Control-Allow-Origin", "*");
        return myHandle(request, response);
    }

    protected abstract Object myHandle(Request request, Response response);

    /**
     * Adapter to handle the serialization/deserialization of JodaTime LocalDate to Json.
     */
    public final class LocalDateAdapter implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
        final org.joda.time.format.DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.date();

        @Override
        public LocalDate deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            return je.getAsString().isEmpty() ? null : DATE_TIME_FORMATTER.parseLocalDate(je.getAsString());
        }

        @Override
        public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDate == null ? StringUtils.EMPTY : DATE_TIME_FORMATTER.print(localDate));
        }
    }

}
