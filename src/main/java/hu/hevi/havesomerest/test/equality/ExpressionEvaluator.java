package hu.hevi.havesomerest.test.equality;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

@Component
public class ExpressionEvaluator {

    public boolean evaluate(String toBeEvaluated, String value) {
        boolean result = true;
        switch (toBeEvaluated) {
            case "#isNumber()":
                result = NumberUtils.isNumber(value);
                break;
            case "#isDigits()":
                result = NumberUtils.isNumber(value);
                break;
            default:
                result = true;
                break;
        }
        return result;
    }
}
