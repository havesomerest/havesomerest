package hu.hevi.havesomerest.test;

import hu.hevi.havesomerest.common.EndPointBuilder;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@Component
@Slf4j
public class TestRunner {

    @Autowired
    private TestProperties testProperties;
    @Autowired
    private ResultLogger resultLogger;
    @Autowired
    private EndPointBuilder endPointBuilder;

    public void runTests(Set<Test> tests) {
        tests.stream().sorted((a, b) -> b.getName().compareTo(a.getName()))
             .forEach(test -> {
                 Optional<ResponseEntity<String>> response = Optional.empty();
                 String endPoint = endPointBuilder.build(test);
                 try {

                     response = getResponse(endPoint, test);

                     response.ifPresent(resp -> {
                         log.debug(resp.getStatusCode().toString() + " -> " + resp.toString());
                         String message = MessageFormat.format("Status -> expected: {0}, actual: {1}",
                                                               resp.getStatusCode().toString(),
                                                               test.getStatusCode());
                         assertTrue(message, resp.getStatusCode().toString().equals(test.getStatusCode()));

                         JSONObject responseObject = new JSONObject(resp.getBody());
                         assertTrue("Test body not equals", test.getResponse().similar(responseObject));

                         resultLogger.logPassed(test, endPoint, resp);
                         log.debug(MessageFormat.format("{0}", test.getDescription()));
                     });

                 } catch (AssertionError e) {
                     resultLogger.logFailed(test, "", e.getMessage());
                 } catch (HttpClientErrorException e) {
                     resultLogger.logFailed(test, endPoint, e);
                 }
             });
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
