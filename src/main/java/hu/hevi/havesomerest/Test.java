package hu.hevi.havesomerest;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
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
    private ScriptObjectMirror request;
    private ScriptObjectMirror response;
}
