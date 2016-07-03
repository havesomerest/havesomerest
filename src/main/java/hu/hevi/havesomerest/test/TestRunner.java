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
        tests.forEach(test -> {
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

            ScriptObjectMirror responseObject = jsonConverter.convertToObject(response.getBody());

            Boolean equals = equals(test.getResponse(), responseObject);



            System.out.println("Assert: " + equals);

            System.out.println(MessageFormat.format("{0} -> {1}", test.getStatusCode(), response.getStatusCode().toString()));
            System.out.println(MessageFormat.format("{0}", test.getDescription()));
        });
    }

    private Boolean equals(ScriptObjectMirror test, ScriptObjectMirror other) {
        Boolean equals = true;
        for (String key : test.keySet()) {
            if (isScriptObjectMirror(test, key) && isScriptObjectMirror(other, key)) {
                equals((ScriptObjectMirror) test.get(key), (ScriptObjectMirror) other.get(key));
            } else if (isScriptObjectMirror(test, key) && !isScriptObjectMirror(other, key)) {
                equals = false;
                break;
            }
            if (!other.containsKey(key) || !other.get(key).equals(test.get(key))) {
                equals = false;
                break;
            }
        }
        return equals;
    }

    private boolean isScriptObjectMirror(ScriptObjectMirror test, String key) {
        return test.get(key).getClass().equals(ScriptObjectMirror.class);
    }
}
