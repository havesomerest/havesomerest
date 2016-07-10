package hu.hevi.havesomerest.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.text.MessageFormat;

@Slf4j
@Component
public class ResultLogger {

    void logPassed(Test test, String finalEndPoint, ResponseEntity<String> resp) {
        log.info(String.format("PASSED -> %-4s %3s /%-15s - %-35s",
                                      test.getMethod().toString().toUpperCase(),
                                      resp.getStatusCode().toString(),
                                      finalEndPoint,
                                      test.getName()));
    }

    void logFailed(Test test, String finalEndPoint, HttpClientErrorException e) {
        String format = String.format("FAILED -> %-4s %3s /%-15s - %-35s -> %s",
                                      test.getMethod().toString().toUpperCase(),
                                      test.getStatusCode(),
                                      finalEndPoint,
                                      test.getName(),
                                      e.getStatusCode() + " " + e.getStatusText());
        log.error(format);
    }
}
