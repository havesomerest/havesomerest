package hu.hevi.havesomerest;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ToTestConverter {

    private static final String RESPONSE = "response";
    private static final String REQUEST = "request";
    private static final String JSON_SUFFIX = ".json";

    @Autowired
    private JsBasedJsonConverter jsonConverter;

    Set<Test> convert(Map<Path, Optional<TestDirectory.TestDirectoryBuilder>> filesByDirectory) {

        List<TestDirectory> testDirectories = new ArrayList<>();

        filesByDirectory.entrySet().forEach((en) -> {
            en.getValue().ifPresent(testDirectoryBuilder -> {
                TestDirectory testDirectory = testDirectoryBuilder.build();
                testDirectories.add(testDirectory);
            });

        });

        Map<String, Test> testsByFilename = getTests(testDirectories);

        testsByFilename.keySet().forEach(filename -> {
            String statusCode = getStatusCodeFromFilename(filename);

            Test test = testsByFilename.get(filename);
            test.setStatusCode(statusCode);
        });

        Set<Test> tests = testsByFilename.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());

        return tests;
    }

    private String getStatusCodeFromFilename(String filename) {
        String statusCode = "";
        if (filename.toLowerCase().startsWith("get")) {
            statusCode = filename.substring(3, 6);
        }
        if (filename.toLowerCase().startsWith("post")) {
            statusCode = filename.substring(4, 7);
        }
        return statusCode;
    }

    Map<String, Test> getTests(List<TestDirectory> testDirectories) {
        Map<String, Test> testByFilename = new HashMap<>();
        testDirectories.stream()
                       .map(TestDirectory::getTestCases)
                       .forEach(testCase -> {
                           testCase.forEach(test -> {
                               try {
                                   String fileContent = new String(Files.readAllBytes(test.getPath()));
                                   if (isJson(test)) {
                                       ScriptObjectMirror convertedObject = jsonConverter.convertToObject(fileContent);

                                       if (isTestFile(test)) {
                                           Test t = getTest(convertedObject);
                                           testByFilename.put(test.getFileName(), t);
                                       }
                                   }
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           });
                       });
        return testByFilename;
    }

    private Test getTest(ScriptObjectMirror fileContent) {
        Test.TestBuilder testBuilder = Test.builder();
        if (fileContent.containsKey(REQUEST)) {
            testBuilder.request((ScriptObjectMirror) fileContent.get(REQUEST)).build();
        }
        if (fileContent.containsKey(RESPONSE)) {
            testBuilder.response((ScriptObjectMirror) fileContent.get(RESPONSE)).build();
        }
        if (fileContent.containsKey("description")) {

            testBuilder.description(((String) fileContent.get("description").toString())).build();
        }
        return testBuilder.build();
    }

    private boolean isTestFile(TestCase f) {
        String asd = f.getFileName();
        String[] split = asd.split("[.]");
        boolean isTestFile = false;
        if (split.length == 2 && "json".equals(split[1])) {
            isTestFile = true;
        }
        return isTestFile;
    }

    private boolean isJson(TestCase f) {
        return f.getFileName().endsWith(JSON_SUFFIX);
    }
}


