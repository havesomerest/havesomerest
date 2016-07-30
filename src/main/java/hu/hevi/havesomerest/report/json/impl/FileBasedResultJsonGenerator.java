package hu.hevi.havesomerest.report.json.impl;

import hu.hevi.havesomerest.report.json.ResultJsonGenerator;
import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class FileBasedResultJsonGenerator implements ResultJsonGenerator {

    public static final String RESULT_FILE_PATH_NAME = "target/havesomerest/result.json";

    @Override
    public void generateResult(Map<Test, JSONObject> tests, Map<Test, TestResult> results) throws IOException {
        JSONArray logEntries = new JSONArray();
        results.keySet().forEach(key -> {
            Test test = key;
            TestResult testResult = results.get(key);

            JSONObject testFileEntry = new JSONObject();
            JSONObject testFile = tests.get(test);
            testFileEntry.put("testCase", testFile);

            JSONObject actualResponse = new JSONObject();
            actualResponse.put("body", testResult.getResponseBody());
            actualResponse.put("headers", testResult.getResponseHeaders());

            actualResponse.put("statusCode", testResult.getStatusCodeString());

            JSONObject logEntry = new JSONObject();
            logEntry.put("resultType", testResult.getResultType());
            logEntry.put("testCase", testFile);
            logEntry.put("actualResponse", actualResponse);

            logEntries.put(logEntry);
        });

        Optional<Path> resultFile = Optional.empty();

        if (!Paths.get(RESULT_FILE_PATH_NAME).toFile().exists()) {
            Files.createDirectories(Paths.get(RESULT_FILE_PATH_NAME).getParent());
            resultFile = Optional.of(Files.createFile(Paths.get(RESULT_FILE_PATH_NAME)));
        } else {
            resultFile = Optional.of(Paths.get(RESULT_FILE_PATH_NAME));
        }

        Optional<Path> finalResultFile = resultFile;
        resultFile.ifPresent(result -> {
            try {
                try (BufferedWriter writer = Files.newBufferedWriter(finalResultFile.get())) {
                    writer.write(logEntries.toString(2));
                }
            } catch (IOException e) {
                log.warn("Couldn't open result report file.");
            }

            log.info("logging results in file");
        });
    }
}
