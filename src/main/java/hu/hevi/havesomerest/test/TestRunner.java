package hu.hevi.havesomerest.test;

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

    public void runTests(Set<Test> tests) {
        tests.stream().sorted((a, b) -> b.getName().compareTo(a.getName()))
             .forEach(test -> {
                 try {
                     RestTemplate restTemplate = new RestTemplate();

                     HttpHeaders headers = test.getRequestHeaders();

                     String requestBody = test.getRequest().toString();

                     HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

                     HttpMethod httpMethod = HttpMethod.valueOf(test.getMethod().name().toUpperCase());

                     String endPoint = "";
                     for (String part : test.getEndpointParts()) {
                         if (part.startsWith("_") && part.endsWith("_")) {
                             endPoint = endPoint + "/" + test.getPathVariablesByName().get(part);
                         } else {
                             endPoint = endPoint + part;
                         }
                     }

                     String httpUrl = testProperties.getTestServerHost() + endPoint;
                     UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(httpUrl);
                     test.getRequestParams().keySet().forEach(key -> {
                        uriComponentsBuilder.queryParam(key, test.getRequestParams().get(key));
                     });

                     URI uri = uriComponentsBuilder.build().encode().toUri();

                     Optional<ResponseEntity<String>> response = Optional.empty();
                     String finalEndPoint = endPoint;
                     try {
                         response = Optional.ofNullable(restTemplate.exchange(
                                 uri,
                                 httpMethod,
                                 entity,
                                 String.class));
                     } catch (HttpClientErrorException e) {
                         resultLogger.logFailed(test, finalEndPoint, e);
                     }

                     response.ifPresent(resp -> {
                         log.debug(resp.getStatusCode().toString() + " -> " + resp.toString());
                         assertTrue("Test status not equals", resp.getStatusCode().toString().equals(test.getStatusCode()));

                         JSONObject responseObject = new JSONObject(resp.getBody());
                         assertTrue("Test body not equals", test.getResponse().similar(responseObject));

                         resultLogger.logPassed(test, finalEndPoint, resp);
                         log.debug(MessageFormat.format("{0}", test.getDescription()));
                     });

                 } catch (AssertionError e) {
                     resultLogger.logFailed(test, "", e.getMessage());
                 } catch (HttpClientErrorException e) {
                     log.error(e.getMessage());
                 }
             });
    }


}
