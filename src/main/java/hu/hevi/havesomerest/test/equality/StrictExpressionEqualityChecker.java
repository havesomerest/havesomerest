package hu.hevi.havesomerest.test.equality;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StrictExpressionEqualityChecker {

    @Autowired
    private ExpressionEvaluator evaluator;
    @Autowired
    private JsonObjectHelper jsonObjectHelper;

    public StrictExpressionEqualityChecker() {
    }

    public StrictExpressionEqualityChecker(ExpressionEvaluator evaluator, JsonObjectHelper jsonObjectHelper) {
        this.evaluator = evaluator;
        this.jsonObjectHelper = jsonObjectHelper;
    }

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

        try {
            if (jsonObjectHelper.isJsonArray(expected, key)) {
                JSONArray expectedArray = (JSONArray) expected.get(key);
                JSONArray actualArray = (JSONArray) actual.get(key);
                equals = expectedArray.similar(actualArray);
            } else if (jsonObjectHelper.isJsonObject(expected, key)) {
                equals = this.equals((JSONObject) expected.get(key), (JSONObject) actual.get(key));
            } else if (checkForExpression && isExpresstion((String) expected.get(key))) {
                equals = evaluator.evaluate((String) expected.get(key), actual.get(key));
            } else if (!hasKey(actual, key) || !isValueEquals(expected, actual, key)) {
                if (actual.has(key) && isExpresstion((String) actual.get(key))) {
                    equals = evaluator.evaluate((String) expected.get(key), (String) actual.get(key));
                } else {
                    equals = false;
                }
            }
        } catch (ClassCastException e) {
            log.error(e.getMessage());
            equals = false;
        }
        return equals;
    }

    private boolean isValueEquals(JSONObject expected, JSONObject actual, String key) {
        return expected.get(key).equals(actual.get(key));
    }

    private boolean hasKey(JSONObject actual, String key) {
        return actual.has(key);
    }

    private boolean isInteger(String actual) {
        Integer.parseInt(actual);
        return false;
    }

    private boolean isExpresstion(String string) {
        return string.startsWith("#") && string.endsWith("()");
    }
}
