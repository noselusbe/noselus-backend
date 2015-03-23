package be.noselus.service;

import com.google.gson.*;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import spark.ResponseTransformer;

import java.lang.reflect.Type;

/**
 * Response transformer route that is in charge of converting object response into Json response.
 */
public class JsonTransformer implements ResponseTransformer {

    private final Gson gson;

    public JsonTransformer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
    }

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

    /**
     * Adapter to handle the serialization/deserialization of JodaTime LocalDate to Json.
     */
    public static final class LocalDateAdapter implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
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
