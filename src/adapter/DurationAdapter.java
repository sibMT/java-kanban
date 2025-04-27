package adapter;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;


public class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration duration, Type type, JsonSerializationContext context) {
        return duration == null ? JsonNull.INSTANCE : new JsonPrimitive(duration.toMinutes());
    }

    @Override
    public Duration deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        return json.isJsonNull() ? null : Duration.ofMinutes(json.getAsLong());
    }
}
