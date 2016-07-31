package hu.hevi.havesomerest.test.equality;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExpressionEvaluator {

    @Autowired
    private JsonObjectHelper jsonObjectHelper;

    public ExpressionEvaluator() {
    }

    public ExpressionEvaluator(JsonObjectHelper jsonObjectHelper) {
        this.jsonObjectHelper = jsonObjectHelper;
    }

    public boolean evaluate(String toBeEvaluated, Object value) {
        boolean result = false;
        if (jsonObjectHelper.isJsonObject(value)) {
            result = evaluateOnJsonObject(toBeEvaluated, (JSONObject) value);
        } else if (jsonObjectHelper.isJsonArray(value)) {
            result = evaluateOnJsonArray(toBeEvaluated, (JSONArray) value);
        } else {
            result = evaluateString(toBeEvaluated, (String) value);
        }

        return result;
    }

    private boolean evaluateOnJsonArray(String toBeEvaluated, JSONArray value) {
        boolean result = true;
        switch (toBeEvaluated) {
            case "#isArray()":
                result = value instanceof JSONArray;
                break;
            case "#isPresent()":
                result = value != null;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    private boolean evaluateOnJsonObject(String toBeEvaluated, JSONObject value) {
        boolean result = true;
        switch (toBeEvaluated) {
            case "#isObject()":
                result = value instanceof JSONObject;
                break;
            case "#isPresent()":
                result = value != null;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    private boolean evaluateString(String toBeEvaluated, String value) {
        boolean result = true;
        switch (toBeEvaluated) {
            case "#isNumber()":
                result = NumberUtils.isNumber(value);
                break;
            case "#isDigits()":
                result = NumberUtils.isNumber(value);
                break;
            case "#notEmpty()":
                result = StringUtils.isNotEmpty(value);
                break;
            case "#isPresent()":
                result = value != null;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
}
