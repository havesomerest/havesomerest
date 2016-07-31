package hu.hevi.havesomerest.test.equality;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JsonObjectHelper {

    boolean isJsonArray(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONArray.class);
    }

    boolean isJsonArray(Object expected, Object actual) {
        return expected instanceof JSONArray && actual instanceof JSONArray;
    }

    boolean isValueJsonArray(Object expected, Object actual, String key) {
        boolean equals = true;
        try {
            JSONObject expectedObject = (JSONObject) expected;
            JSONObject actualObject = (JSONObject) actual;
            equals = expectedObject.get(key) instanceof JSONArray && actualObject.get(key) instanceof JSONArray;
        } catch (ClassCastException e) {
            equals = false;
        }
        return equals;
    }

    boolean isJsonObject(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONObject.class);
    }

    boolean isJsonObject(Object expected, Object actual) {
        return expected instanceof JSONObject && actual instanceof JSONObject;
    }

    boolean isValueJsonObject(Object expected, Object actual, String key) {
        boolean equals = true;
        try {
            JSONObject expectedObject = (JSONObject) expected;
            JSONObject actualObject = (JSONObject) actual;
            equals = expectedObject.get(key) instanceof JSONObject && actualObject.get(key) instanceof JSONObject;
        } catch (ClassCastException e) {
            equals = false;
        }
        return equals;
    }

    boolean isJsonArray(Object obj) {
        return obj instanceof JSONArray;
    }

    boolean isJsonObject(Object obj) {
        return obj instanceof JSONObject;
    }
}
