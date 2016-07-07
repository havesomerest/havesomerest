package hu.hevi.havesomerest.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Slf4j
@Component
public class TestLogger {

    void logPassed(Test test, String finalEndPoint, ResponseEntity<String> resp) {
        log.info(MessageFormat.format("PASSED -> {0} /{1} - {2}", resp.getStatusCode().toString(), finalEndPoint, test.getName()));
    }

    void logFailed(String format) {
        log.error(format);
    }
}