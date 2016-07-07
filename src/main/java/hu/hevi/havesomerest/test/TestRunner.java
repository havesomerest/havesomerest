package hu.hevi.havesomerest.test;

import hu.hevi.havesomerest.converter.JsBasedJsonConverter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@Component
@Slf4j
public class TestRunner {

    public static final String TEST_SERVER_HOST = "http://localhost:8080/";

    @Autowired
    private JsBasedJsonConverter jsonConverter;
    @Autowired
    private TestLogger testLogger;

    public void runTests(Set<Test> tests) {
        tests.stream().sorted((a, b) -> a.getName().compareTo(b.getName()))
             .forEach(test -> {
                 try {
                     log.debug(test.getRequest().entrySet().toString());

                     RestTemplate restTemplate = new RestTemplate();

                     HttpHeaders headers = new HttpHeaders();
                     headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);


                     HttpEntity<?> entity = new HttpEntity<>(headers);

                     HttpMethod httpMethod = HttpMethod.GET;
                     switch (test.getMethod().name().toUpperCase()) {
                         case "GET":
                             httpMethod = HttpMethod.GET;
                             break;
                         case "POST":
                             httpMethod = HttpMethod.POST;
                             break;
                         case "PUT":
                             httpMethod = HttpMethod.PUT;
                             break;
                         case "PATCH":
                             httpMethod = HttpMethod.PATCH;
                             break;
                         case "DELETE":
                             httpMethod = HttpMethod.DELETE;
                             break;

                     }

                     String endPoint = "";
                     for (String part : test.getEndpointParts()) {
                         if (part.startsWith("_") && part.endsWith("_")) {
                             endPoint = endPoint + "/" + test.getPathVariablesByName().get(part);
                         } else {
                             endPoint = endPoint + part;
                         }
                     };

                     String httpUrl = TEST_SERVER_HOST + endPoint;
                     UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(httpUrl);
//                                                                   .queryParam("msisdn", msisdn);

                     Optional<ResponseEntity<String>> response = Optional.empty();

                     String finalEndPoint = endPoint;
                     try {
                         response = Optional.ofNullable(restTemplate.exchange(
                                 builder.build().encode().toUri(),
                                 httpMethod,
                                 entity,
                                 String.class));
                     } catch (HttpClientErrorException e) {
                         testLogger.logFailed(MessageFormat.format("FAILED -> {0} /{1} - {2} -> {3}", test.getStatusCode(), finalEndPoint, test.getName(),
                                                        e.getStatusCode() + " " + e.getStatusText()));
                     }

                     response.ifPresent(resp -> {
                         log.debug(resp.getStatusCode().toString() + " -> " + resp.toString());
                         assertTrue(resp.getStatusCode().toString().equals(test.getStatusCode()));

                         JsonValue responseObject = jsonConverter.convertToObject(resp.getBody());
                         assertTrue(equalsJson(test.getResponse(), responseObject));

                         testLogger.logPassed(test, finalEndPoint, resp);
                         log.debug(MessageFormat.format("{0}", test.getDescription()));
                     });

                 } catch (AssertionError e) {
                     log.error("FAILED - " + test.getName());
                 } catch (HttpClientErrorException e) {
                     log.error(e.getMessage());
                 }
             });
    }

    private Boolean equalsJson(JsonValue expected, JsonValue actual) {
        Boolean equals = true;
        for (String child : expected.children()) {
            if (expected.isJsonValue(child)) {
                return equalsJson(new JsonValue((ScriptObjectMirror) expected.get(child)), new JsonValue((ScriptObjectMirror) actual.get(child)));
            }
            if (!expected.isJsonValue(child) && !expected.get(child).equals(actual.get(child))) {
                log.debug("FALSE");
                return false;
            }
            log.debug(MessageFormat.format("{0} -> {1} : {2} -> {3}, equalsJson: {4}",
                                           child, expected.get(child), actual.get(child),
                                           expected.isJsonValue(child) ? "jsonValue" : "String",
                                           expected.get(child).equals(actual.get(child))));
        }
        return equals;
    }

//    private Boolean equalsJson(JsonValue expected, JsonValue actual) {
//        Boolean equalsJson = true;
//        for (String key : expected.children()) {
//
//            if (expected.isJsonValue(key) && actual.isJsonValue(key)) {
//                log.debug("Recursion!");
//                equalsJson(new JsonValue((ScriptObjectMirror) expected.get(key)), new JsonValue((ScriptObjectMirror) actual.get(key)));
//            } else if (expected.isJsonValue(key) && !actual.isJsonValue(key)) {
//                equalsJson = false;
//                break;
//            }
//            if (!actual.containsKey(key) || !actual.get(key).equalsJson(expected.get(key))) {
//                equalsJson = false;
//                break;
//            }
//        }
//        return equalsJson;
//    }

}
