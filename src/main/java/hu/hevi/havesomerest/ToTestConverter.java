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

        Set<Test> tests = getTests(testDirectories).entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());

        return tests;
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

    private Test getTest(ScriptObjectMirror convert) {
        Test.TestBuilder testBuilder = Test.builder();
        if (convert.containsKey(REQUEST)) {
            testBuilder.request((ScriptObjectMirror) convert.get(REQUEST)).build();
        }
        if (convert.containsKey(RESPONSE)) {
            testBuilder.response((ScriptObjectMirror) convert.get(RESPONSE)).build();
        }
        return testBuilder.build();
    }

    private boolean isTestFile(TestCase f) {
        String asd = f.getFileName();
        String[] split = asd.split("[.]");
        boolean isTestFile = false;
        if (split.length > 1 && "json".equals(split[1])) {
            isTestFile = true;
        }
        return isTestFile;
    }

    private boolean isJson(TestCase f) {
        return f.getFileName().endsWith(JSON_SUFFIX);
    }
}


