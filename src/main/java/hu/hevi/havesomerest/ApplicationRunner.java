package hu.hevi.havesomerest;

import hu.hevi.havesomerest.common.EndPointNameBuilder;
import hu.hevi.havesomerest.converter.ToTestConverter;
import hu.hevi.havesomerest.io.StructureReader;
import hu.hevi.havesomerest.io.TestDirectory;
import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;
import hu.hevi.havesomerest.test.TestRunner;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
class ApplicationRunner {

    public static final String RESULT_FILE_PATH_NAME = "target/havesomerest/result.json";
    @Autowired
    private StructureReader structureReader;
    @Autowired
    private ToTestConverter toTestConverter;
    @Autowired
    private TestRunner testRunner;
    @Autowired
    private Environment environment;
    @Autowired
    private EndPointNameBuilder endPointNameBuilder;

    void run() {
        try {

            Map<Path, Optional<TestDirectory>> filesByDirectory = structureReader.getStructure();
            Map<Test, JSONObject> tests = toTestConverter.convert(filesByDirectory);
            Map<Test, TestResult> results = testRunner.runTests(tests.keySet());

            JSONArray logEntries = new JSONArray();
            results.keySet().forEach(key -> {
                Test test = key;
                TestResult testResult = results.get(key);

                JSONObject testFileEntry = new JSONObject();
                JSONObject testFile = tests.get(test);
                testFileEntry.put("testCase", testFile);

                JSONObject responseEntry = new JSONObject();
                responseEntry.put("body", testResult.getResponseBody());
                responseEntry.put("headers", testResult.getResponseHeaders());
                responseEntry.put("statusCode", testResult.getStatusCode().toString());

                JSONObject logEntry = new JSONObject();
                logEntry.put("resultType", testResult.getResultType());
                logEntry.put("request", testFileEntry);
                logEntry.put("response", responseEntry);

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


            log.debug(environment.containsProperty("asdf") + " : " + environment.getProperty("asdf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
