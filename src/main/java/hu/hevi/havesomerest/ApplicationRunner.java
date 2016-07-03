package hu.hevi.havesomerest;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
class ApplicationRunner {

    @Autowired
    private StructureReader structureReader;
    @Autowired
    private ToTestConverter toTestConverter;
    @Autowired
    private JsBasedJsonConverter jsonConverter;

    void run() {
        try {

            Map<Path, Optional<TestDirectory.TestDirectoryBuilder>> filesInDirectory = structureReader.getStructure();
            Set<Test> tests = toTestConverter.convert(filesInDirectory);

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

                ScriptObjectMirror scriptObjectMirror = jsonConverter.convertToObject(response.getBody());


                Boolean equals = false;
                for (String s : scriptObjectMirror.keySet()) {
                    if (test.getResponse().containsKey(s) && scriptObjectMirror.get(s).equals(test.getResponse().get(s))) {
                        equals = true;
                    }
                }



                System.out.println("Assert: " + equals);


                System.out.println(MessageFormat.format("{0} -> {1}", test.getStatusCode(), response.getStatusCode().toString()));
                System.out.println(MessageFormat.format("{0}", test.getDescription()));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
