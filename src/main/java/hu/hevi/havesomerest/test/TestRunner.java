package hu.hevi.havesomerest.test;

import hu.hevi.havesomerest.common.EndPointNameBuilder;
import hu.hevi.havesomerest.config.TestProperties;
import hu.hevi.havesomerest.test.equality.StrictExpressionEqualityChecker;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

    private static final int TIMEOUT = 1000;
    @Autowired
    private TestProperties testProperties;
    @Autowired
    private ResultLogger resultLogger;
    @Autowired
    private EndPointNameBuilder endPointNameBuilder;
    @Autowired
    private StrictExpressionEqualityChecker equalityChecker;

    public Map<Test, TestResult> runTests(Collection<Test> tests) {

        log.info("Running " + tests.size() + " test scenarios\n");

        Map<Test, TestResult> testResults = new HashMap<>();
        tests.stream()
             .sorted((a, b) -> b.getName().compareTo(a.getName()))
             .forEach(test -> {
                 TestResult.TestResultBuilder testResultBuilder = TestResult.builder();
                 Optional<ResponseEntity<String>> response = Optional.empty();
                 String endPoint = endPointNameBuilder.build(test.getEndpointParts());
                 try {

                     response = fireRequest(endPoint, test);

                     response.ifPresent(actualResponse -> {
                         log.debug(actualResponse.getStatusCode().toString() + " -> " + actualResponse.toString());


                         JSONObject responseObject = new JSONObject();
                         if (actualResponse.hasBody()) {
                             responseObject = new JSONObject(actualResponse.getBody());
                         }

                         testResultBuilder.statusCode(actualResponse.getStatusCode())
                                          .responseBody(responseObject)
                                          .responseHeaders(actualResponse.getHeaders());

                         performAssertion(test, endPoint, actualResponse, responseObject);
                         resultLogger.logPassed(test, endPoint, actualResponse);

                         log.debug(MessageFormat.format("{0}", test.getDescription()));

                         testResultBuilder.resultType(ResultType.PASSED);
                     });

                     response.orElseThrow(() -> new AssertionError("actualResponse is not present"));

                 } catch (AssertionError e) {
                     testResultBuilder.resultType(ResultType.FAILED);
                     resultLogger.logFailed(test, "", e.getMessage());
                 } catch (HttpClientErrorException e) {
                     testResultBuilder.resultType(ResultType.FAILED);
                     resultLogger.logFailed(test, endPoint, e);
                 } catch (Exception e) {
                     testResultBuilder.resultType(ResultType.FAILED);
                     resultLogger.logFailed(test, endPoint, e.getMessage());
                 }
                 finally {
                     TestResult testResult = testResultBuilder.build();
                     testResults.put(test, testResult);
                 }
             });
        return testResults;
    }

    private void performAssertion(Test test, String endPoint, ResponseEntity<String> actualResponse, JSONObject responseObject) {
        String message = MessageFormat.format("Status -> expected: {0}, actual: {1}",
                                              actualResponse.getStatusCode().toString(),
                                              test.getStatusCode());
        assertTrue(message, actualResponse.getStatusCode().toString().equals(test.getStatusCode()));
        assertTrue("Test body not equals", equalityChecker.equals(new JSONObject(test.getResponse()), responseObject));
        assertTrue("Test headers keys not equals", actualResponse.getHeaders().keySet().containsAll(test.getResponseHeaders().keySet()));
    }

    private Optional<ResponseEntity<String>> fireRequest(String endPoint, Test test) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = test.getRequestHeaders();

        String requestBody = "";
        if (test.hasRequest()) {
            requestBody = test.getRequest().toString();
        }

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

        log.info(MessageFormat.format("Sending {0} request to: {1}\nbody: {2}",
                 httpMethod.toString(),
                 uri.toString()),
                 test.getRequest());

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT);
        requestFactory.setReadTimeout(TIMEOUT);

        restTemplate.setRequestFactory(requestFactory);

        response = Optional.ofNullable(restTemplate.exchange(
                uri,
                httpMethod,
                entity,
                String.class));

        return response;
    }
}
