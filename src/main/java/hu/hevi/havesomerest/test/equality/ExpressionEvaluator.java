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
        boolean result = true;
        switch (toBeEvaluated) {
            case "isNumber()":
                result = NumberUtils.isNumber((String) value);
                break;
            case "isString()":
                result = value instanceof String;
                break;
            case "notEmpty()":
                result = StringUtils.isNotEmpty((String) value);
                break;
            case "isPresent()":
                result = value != null;
                break;
            case "isArray()":
                result = value instanceof JSONArray;
                break;
            case "isObject()":
                result = value instanceof JSONObject;
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
}
