package adapter;

import classes.TaskStatus;
import com.google.gson.*;

import java.lang.reflect.Type;


public class TaskStatusAdapter implements JsonSerializer<TaskStatus>, JsonDeserializer<TaskStatus> {

    @Override
    public TaskStatus deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            return TaskStatus.valueOf(json.getAsString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid TaskStatus value: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(TaskStatus status, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(status.name().toUpperCase());
    }
}