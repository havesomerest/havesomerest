package hu.hevi.havesomerest.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
public class ResultLogger {

    public static final String GREEN = (char) 27 + "[32m";
    public static final String RED = (char) 27 + "[31m";
    public static final String CLEAR = (char) 27 + "[0m";

    void logPassed(Test test, String finalEndPoint, ResponseEntity<String> resp) {
        String info = String.format(GREEN + "PASSED -> %-4s %3s %-25s - %-35s\n" + CLEAR,
                                    test.getMethod().toString().toUpperCase(),
                                    resp.getStatusCode().toString(),
                                    finalEndPoint,
                                    test.getName());
        log.info(info);
    }

    void logFailed(Test test, String finalEndPoint, HttpClientErrorException e) {
        String error = String.format(RED + "FAILED -> %-4s %3s %-25s - %-35s -> %s\n" + CLEAR,
                                     test.getMethod().toString().toUpperCase(),
                                     test.getStatusCode(),
                                     finalEndPoint,
                                     test.getName(),
                                     e.getStatusCode() + " " + e.getStatusText());
        log.error(error);
    }

    void logFailed(Test test, String finalEndPoint, String message) {
        String error = String.format(RED + "FAILED -> %-4s %3s %-25s - %-35s -> %s\n" + CLEAR,
                                     test.getMethod().toString().toUpperCase(),
                                     test.getStatusCode(),
                                     finalEndPoint,
                                     test.getName(),
                                     message);
        log.error(error);
    }
}
