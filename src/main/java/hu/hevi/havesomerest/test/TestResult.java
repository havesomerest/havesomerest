package hu.hevi.havesomerest.test;

import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class TestResult {

    private HttpStatus statusCode;
    private HttpHeaders responseHeaders;
    private JSONObject responseBody;
    private ResultType resultType;
}
