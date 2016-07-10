package hu.hevi.havesomerest.test;

import hu.hevi.havesomerest.config.TestProperties;
import hu.hevi.havesomerest.converter.JsBasedJsonConverter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
    private JsBasedJsonConverter jsonConverter;
    @Autowired
    private ResultLogger resultLogger;

    public void runTests(Set<Test> tests) {
        tests.stream().sorted((a, b) -> b.getName().compareTo(a.getName()))
             .forEach(test -> {
                 try {
                     log.debug(test.getRequest().entrySet().toString());

                     RestTemplate restTemplate = new RestTemplate();

                     HttpHeaders headers = test.getRequestHeaders();

                     HttpEntity<?> entity = new HttpEntity<>(headers);


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
                         resultLogger.logFailed(MessageFormat.format("FAILED -> {0} {1} /{2} - {3} -> {4}",
                                                                     test.getMethod().toString().toUpperCase(),
                                                                     test.getStatusCode(),
                                                                     finalEndPoint,
                                                                     test.getName(),
                                                                     e.getStatusCode() + " " + e.getStatusText()));
                     }

                     response.ifPresent(resp -> {
                         log.debug(resp.getStatusCode().toString() + " -> " + resp.toString());
                         assertTrue(resp.getStatusCode().toString().equals(test.getStatusCode()));

                         JsonValue responseObject = jsonConverter.convertToObject(resp.getBody());
                         assertTrue(equalsJson(test.getResponse(), responseObject));

                         resultLogger.logPassed(test, finalEndPoint, resp);
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
