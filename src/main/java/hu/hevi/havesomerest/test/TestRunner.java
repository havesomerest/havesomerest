package hu.hevi.havesomerest.test;

import hu.hevi.havesomerest.common.EndPointNameBuilder;
import hu.hevi.havesomerest.config.TestProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@Component
@Slf4j
public class TestRunner {

    @Autowired
    private TestProperties testProperties;
    @Autowired
    private ResultLogger resultLogger;
    @Autowired
    private EndPointNameBuilder endPointNameBuilder;

    public Map<Test, TestResult> runTests(Collection<Test> tests) {
        Map<Test, TestResult> testResults = new HashMap<>();
        tests.stream().sorted((a, b) -> b.getName().compareTo(a.getName()))
             .forEach(test -> {
                 TestResult.TestResultBuilder testResultBuilder = TestResult.builder();
                 Optional<ResponseEntity<String>> response = Optional.empty();
                 String endPoint = endPointNameBuilder.build(test);
                 try {

                     response = getResponse(endPoint, test);

                     response.ifPresent(actualResponse -> {
                         log.debug(actualResponse.getStatusCode().toString() + " -> " + actualResponse.toString());
                         String message = MessageFormat.format("Status -> expected: {0}, actual: {1}",
                                                               actualResponse.getStatusCode().toString(),
                                                               test.getStatusCode());

                         JSONObject responseObject = new JSONObject(actualResponse.getBody());

                         testResultBuilder.statusCode(actualResponse.getStatusCode())
                                          .responseBody(responseObject)
                                          .responseHeaders(actualResponse.getHeaders());

                         assertTrue(message, actualResponse.getStatusCode().toString().equals(test.getStatusCode()));

                         assertTrue("Test body not equals", test.getResponse().similar(responseObject));

                         assertTrue("Test selective body not equals", strictEquals(test.getResponse(), responseObject));

                         assertTrue("Test headers keys not equals", actualResponse.getHeaders().keySet().containsAll(test.getResponseHeaders().keySet()));

                         resultLogger.logPassed(test, endPoint, actualResponse);
                         log.debug(MessageFormat.format("{0}", test.getDescription()));
                     });

                 } catch (AssertionError e) {
                     resultLogger.logFailed(test, "", e.getMessage());
                 } catch (HttpClientErrorException e) {
                     resultLogger.logFailed(test, endPoint, e);
                 } finally {
                     TestResult testResult = testResultBuilder.build();
                     testResults.put(test, testResult);
                 }
             });
        return testResults;
    }

    boolean strictEquals(JSONObject expected, JSONObject actual) {
        final Boolean[] equals = {true};
        expected.keySet().forEach(key -> {
            equals[0] = isEquals(expected, actual, key, true);
        });

        actual.keySet().forEach(key -> {
            equals[0] = isEquals(actual, expected, key, false);
        });
        return equals[0];
    }

    private boolean isInteger(String actual) {
        Integer.parseInt(actual);
        return false;
    }

    private boolean isEquals(JSONObject expected, JSONObject actual, String key, boolean checkForExpression) {
        boolean equals = true;

        if (isJsonArray(expected, key)) {
            JSONArray expectedArray = (JSONArray) expected.get(key);
            JSONArray actualArray = (JSONArray) actual.get(key);
            equals = expectedArray.similar(actualArray);
        } else if (isJsonObject(expected, key)) {
            equals = strictEquals((JSONObject) expected.get(key), (JSONObject) actual.get(key));
        } else if (checkForExpression && isElExpresstion((String) expected.get(key))){
            equals = evaluateIfElExpression((String) expected.get(key), (String) actual.get(key));
        } else if (!hasKey(actual, key) || !isValueEquals(expected, actual, key)) {
            if (actual.has(key) && isElExpresstion((String) actual.get(key))) {
                equals = evaluateIfElExpression((String) expected.get(key), (String) actual.get(key));
            } else {
                equals = false;
            }
        }
        return equals;
    }

    private boolean isElExpresstion(String string) {
        return string.startsWith("#") && string.endsWith("()");
    }

    private boolean evaluateIfElExpression(String toBeEvaluated, String value) {
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

    private boolean isValueEquals(JSONObject expected, JSONObject actual, String key) {
        return expected.get(key).equals(actual.get(key));
    }

    private boolean hasKey(JSONObject actual, String key) {
        return actual.has(key);
    }

    private boolean isJsonObject(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONObject.class);
    }

    private boolean isJsonArray(JSONObject expected, String key) {
        return expected.get(key).getClass().equals(JSONArray.class);
    }


    private Optional<ResponseEntity<String>> getResponse(String endPoint, Test test) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = test.getRequestHeaders();

        String requestBody = test.getRequest().toString();

        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

        HttpMethod httpMethod = HttpMethod.valueOf(test.getMethod().name().toUpperCase());

        String httpUrl = testProperties.getTestServerHost() + endPoint;
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(httpUrl);
        test.getRequestParams().keySet().forEach(key -> {
            String value = test.getRequestParams().get(key);
            uriComponentsBuilder.queryParam(key, value);
        });

        URI uri = uriComponentsBuilder.build().encode().toUri();

        Optional<ResponseEntity<String>> response = Optional.empty();
        String finalEndPoint = endPoint;
        response = Optional.ofNullable(restTemplate.exchange(
                uri,
                httpMethod,
                entity,
                String.class));
        return response;
    }
}
