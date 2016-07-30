package hu.hevi.havesomerest.test.equality;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StrictExpressionEqualityChecker {

    @Autowired
    private ExpressionEvaluator evaluator;

    public boolean equals(JSONObject expected, JSONObject actual) {
        final Boolean[] equals = {true};
        expected.keySet().forEach(key -> {
            equals[0] = isEquals(expected, actual, key, true);
        });

        actual.keySet().forEach(key -> {
            equals[0] = isEquals(actual, expected, key, false);
        });
        return equals[0];
    }

    private boolean isEquals(JSONObject expected, JSONObject actual, String key, boolean checkForExpression) {
        boolean equals = true;

        if (isJsonArray(expected, key)) {
            JSONArray expectedArray = (JSONArray) expected.get(key);
            JSONArray actualArray = (JSONArray) actual.get(key);
            equals = expectedArray.similar(actualArray);
        } else if (isJsonObject(expected, key)) {
            equals = this.equals((JSONObject) expected.get(key), (JSONObject) actual.get(key));
        } else if (checkForExpression && isExpresstion((String) expected.get(key))){
            equals = evaluator.evaluate((String) expected.get(key), (String) actual.get(key));
        } else if (!hasKey(actual, key) || !isValueEquals(expected, actual, key)) {
            if (actual.has(key) && isExpresstion((String) actual.get(key))) {
                equals = evaluator.evaluate((String) expected.get(key), (String) actual.get(key));
            } else {
                equals = false;
            }
        }
        return equals;
    }

    private boolean isJsonArray(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONArray.class);
    }

    private boolean isValueEquals(JSONObject expected, JSONObject actual, String key) {
        return expected.get(key).equals(actual.get(key));
    }

    private boolean hasKey(JSONObject actual, String key) {
        return actual.has(key);
    }

    private boolean isJsonObject(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONObject.class);
    }

    private boolean isInteger(String actual) {
        Integer.parseInt(actual);
        return false;
    }

    private boolean isExpresstion(String string) {
        return string.startsWith("#") && string.endsWith("()");
    }
}
