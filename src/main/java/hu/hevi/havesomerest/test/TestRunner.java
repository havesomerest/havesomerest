package hu.hevi.havesomerest.test;

import hu.hevi.havesomerest.converter.JsBasedJsonConverter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.Set;

@Component
@Slf4j
public class TestRunner {

    @Autowired
    private JsBasedJsonConverter jsonConverter;

    public void runTests(Set<Test> tests) {
        tests.stream().sorted((a, b) -> a.getName().compareTo(b.getName()))
             .forEach(test -> {
            System.out.println(test.getRequest().entrySet().toString());

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/todo/342");
//                                                                   .queryParam("msisdn", msisdn);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class);

            System.out.println(response.toString());

                 JsonValue responseObject = jsonConverter.convertToObject(response.getBody());

            Boolean equals = equals(test.getResponse(), responseObject);



            System.out.println("Assert: " + equals.toString().toUpperCase());

            System.out.println(MessageFormat.format("{0} -> {1}", test.getStatusCode(), response.getStatusCode().toString()));
            System.out.println(MessageFormat.format("{0}", test.getDescription()));
        });
    }

    private Boolean equals(JsonValue expected, JsonValue actual) {
        Boolean equals = true;
        for (String child : expected.children()) {
            System.out.println(MessageFormat.format("{0} -> {1} : {2} -> {3}, equals: {4}",
                                                    child, expected.get(child), actual.get(child),
                                                    expected.isJsonValue(child) ? "jsonValue" : "String",
                                                    expected.get(child).equals(actual.get(child))));
            if (expected.isJsonValue(child)) {
                return equals(new JsonValue((ScriptObjectMirror) expected.get(child)), new JsonValue((ScriptObjectMirror) actual.get(child)));
            }
            if (!expected.isJsonValue(child) && !expected.get(child).equals(actual.get(child))) {
                System.out.println("FALSE");
                return false;
            }
        }
        return equals;
    }

//    private Boolean equals(JsonValue expected, JsonValue actual) {
//        Boolean equals = true;
//        for (String key : expected.children()) {
//
//            if (expected.isJsonValue(key) && actual.isJsonValue(key)) {
//                System.out.println("Recursion!");
//                equals(new JsonValue((ScriptObjectMirror) expected.get(key)), new JsonValue((ScriptObjectMirror) actual.get(key)));
//            } else if (expected.isJsonValue(key) && !actual.isJsonValue(key)) {
//                equals = false;
//                break;
//            }
//            if (!actual.containsKey(key) || !actual.get(key).equals(expected.get(key))) {
//                equals = false;
//                break;
//            }
//        }
//        return equals;
//    }

}
