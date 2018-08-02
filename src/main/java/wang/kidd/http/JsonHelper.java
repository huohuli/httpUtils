package wang.kidd.http;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 工具类
 *
 * @author apple
 */
public final class JsonHelper {
    private JsonHelper() {
    }

    private static GsonBuilder builder = new GsonBuilder();

    static {
        builder.registerTypeHierarchyAdapter(CharSequence.class,
                new JsonSerializer<CharSequence>() {
                    @Override
                    public JsonElement serialize(CharSequence str, Type type,
                                                 JsonSerializationContext ctx) {
                        if (str == null) {
                            return JsonNull.INSTANCE;
                        }
                        return new JsonPrimitive(str.toString());
                    }
                });
    }

    private static Object convert(JsonElement val) {
        Object result;
        if (val.isJsonNull()) {
            result = null;
        } else if (val.isJsonObject()) {
            result = convertObject((JsonObject) val);
        } else if (val.isJsonArray()) {
            result = convertArray((JsonArray) val);
        } else {
            JsonPrimitive pre = val.getAsJsonPrimitive();
            if (pre.isBoolean()) {
                result = val.getAsBoolean();
            } else if (pre.isNumber()) {
                result = val.getAsBigDecimal();
            } else {
                result = val.getAsString();
            }
        }
        return result;
    }

    private static Map<String, Object> convertObject(JsonObject je) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (Entry<String, JsonElement> entry : je.entrySet()) {
            map.put(entry.getKey(), convert(entry.getValue()));
        }
        return map;
    }

    private static List<Object> convertArray(JsonArray array) {
        List<Object> result = new ArrayList<Object>();
        for (int i = 0; i < array.size(); i++) {
            result.add(convert(array.get(i)));
        }
        return result;
    }

    public static String bean2json(Object bean) {
        Gson gson = builder.create();
        return gson.toJson(bean).toString();
    }

    public static <T> T json2bean(String json, Class<T> cls) {
        Gson gson = builder.create();
        return gson.fromJson(json, cls);
    }

    public static List<Object> json2list(String json) {
        JsonParser parser = new JsonParser();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        JsonElement je = parser.parse(reader);
        if (je.isJsonNull()) {
            return null;
        }
        return convertArray((JsonArray) je);
    }

    public static Map<String, Object> json2map(String json) {
        JsonParser parser = new JsonParser();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        JsonElement je = parser.parse(reader);
        if (je.isJsonNull()) {
            return null;
        }
        return convertObject((JsonObject) je);
    }
}