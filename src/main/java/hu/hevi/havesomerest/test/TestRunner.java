package hu.hevi.havesomerest.test;

import hu.hevi.havesomerest.common.EndPointNameBuilder;
import hu.hevi.havesomerest.config.TestProperties;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;

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
