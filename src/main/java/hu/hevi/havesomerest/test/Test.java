package hu.hevi.havesomerest.test;

import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class Test {

    private String name;
    private String statusCode;
    private List<String> endpointParts;
    private Map<String, String> pathVariablesByName;
    private HttpMethod method;
    private String description;
    private HttpHeaders requestHeaders;
    private Map<String, String> requestParams;
    private JSONObject request;
    private JSONObject response;
}
