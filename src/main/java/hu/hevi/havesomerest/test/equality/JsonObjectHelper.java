package hu.hevi.havesomerest.test.equality;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JsonObjectHelper {

    boolean isJsonArray(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONArray.class);
    }

    boolean isJsonObject(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONObject.class);
    }

    boolean isJsonArray(Object obj) {
        return obj instanceof JSONArray;
    }

    boolean isJsonObject(Object obj) {
        return obj instanceof JSONObject;
    }
}
