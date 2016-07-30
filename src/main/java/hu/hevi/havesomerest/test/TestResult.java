package hu.hevi.havesomerest.test;

import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Data
@Builder
public class TestResult {

    private HttpStatus statusCode;
    private HttpHeaders responseHeaders;
    private JSONObject responseBody;
    private ResultType resultType = ResultType.PASSED;

    public Optional<HttpStatus> getStatusCode() {
        return Optional.ofNullable(statusCode);
    }

    public String getStatusCodeString() {
        return statusCode != null ? statusCode.toString() : "";
    }
}
