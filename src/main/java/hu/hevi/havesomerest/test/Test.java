package hu.hevi.havesomerest.test;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Data
@Builder(toBuilder = true)
public class Test {

    private String name;
    private String statusCode;
    private String description;
    private HttpHeaders headers;
    private Map<String, String> requestParams;
    private JsonValue request;
    private JsonValue response;
}
