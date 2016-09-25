package hu.hevi.havesomerest.test.equality;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
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
        if (expected == null && actual == null) {
            return true;
        } else if (expected == null && actual != null && actual.length() == 0) {
            return true;
        } else if (expected != null && actual == null) {
            return false;
        }

        final Boolean[] equals = {true};

        expected.keySet().forEach(key -> {
            equals[0] = isEquals(expected, actual, key, true);
        });

        if (equals[0] != false) {
            actual.keySet().forEach(key -> {
                equals[0] = isEquals(expected, actual, key, false);
            });
        }
        return equals[0];
    }

    private boolean isEquals(JSONObject expected, JSONObject actual, String key, boolean checkForExpression) {
        boolean equals = true;

        try {
            if (jsonObjectHelper.isValueJsonArray(expected, actual, key)) {
                JSONArray expectedArray = (JSONArray) expected.get(key);
                JSONArray actualArray = (JSONArray) actual.get(key);

                if (expectedArray.length() == 0 && actualArray.length() == 0) {
                    return true;
                }


                final boolean[] result = new boolean[1];
                result[0] = false;
                expectedArray.forEach(e -> {
                    actualArray.forEach(a -> {
                        if (e instanceof String && a instanceof String) {
                            if (e.equals(a)) {
                                result[0] = true;
                            }
                        } else if (e instanceof JSONObject && a instanceof JSONObject) {
                            result[0] = this.equals((JSONObject) e, (JSONObject) a);
                        } else if (e instanceof JSONArray && a instanceof JSONArray) {
                            result[0] = this.equals((JSONObject) expected.get(key), (JSONObject) actual.get(key));
                        }
                    });
                });

                equals = result[0];
            } else if (jsonObjectHelper.isValueJsonObject(expected, actual, key)) {
                equals = this.equals((JSONObject) expected.get(key), (JSONObject) actual.get(key));
            } else if (isBothExpression(expected, actual, key)) {
                equals = true;
            } else if(isBothEmpty(expected, actual, key)) {
                equals = true;
            } else if (checkForExpression && expected.get(key) instanceof String && isExpression((String) expected.get(key))) {
                equals = evaluator.evaluate((String) expected.get(key), actual.get(key));
            } else if (!checkForExpression && actual.get(key) instanceof String && isExpression((String) actual.get(key))) {
                equals = evaluator.evaluate((String) actual.get(key), expected.get(key));
            } else if (checkForExpression && actual.get(key) instanceof String && isExpression((String) actual.get(key))) {
                equals = evaluator.evaluate((String) expected.get(key), actual.get(key));
            } else if ((!hasKey(actual, key) || !isValueEquals(expected, actual, key)) && expected.get(key) instanceof String && actual.get(key) instanceof String && !(isExpression((String) expected.get(key)) || isExpression((String) actual.get(key)))) {
                equals = false;
            } else {
                equals = true;
            }
        } catch (ClassCastException e) {
            equals = false;
        } catch (JSONException e) {
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

    private boolean isExpression(Object object) {
        String string = String.valueOf(object);
        return string.endsWith("()");
    }

    private boolean isBothExpression(Object object, Object object2, String key) {
        boolean isExpression = true;
        try {
            JSONObject jsonObject = (JSONObject) object;
            JSONObject jsonObject2 = (JSONObject) object2;

            isExpression = isExpression(jsonObject.get(key)) && isExpression(jsonObject2.get(key));
        } catch (JSONException e) {
            isExpression = false;
        }

        return isExpression;
    }

    private boolean isBothEmpty(Object object, Object object2, String key) {
        return object == null && object2 == null;
    }
}
