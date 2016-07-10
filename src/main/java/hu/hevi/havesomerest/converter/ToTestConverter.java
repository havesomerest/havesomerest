package hu.hevi.havesomerest.converter;

import hu.hevi.havesomerest.io.TestDirectory;
import hu.hevi.havesomerest.io.TestFile;
import hu.hevi.havesomerest.test.JsonValue;
import hu.hevi.havesomerest.test.Test;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ToTestConverter {

    private static final String RESPONSE = "response";
    private static final String REQUEST = "request";
    private static final String JSON_SUFFIX = ".json";

    @Autowired
    private JsBasedJsonConverter jsonConverter;

    public Set<Test> convert(Map<Path, Optional<TestDirectory>> filesByDirectory) {

        List<TestDirectory> testDirectories = new ArrayList<>();
        filesByDirectory.entrySet().forEach((en) -> {
            en.getValue().ifPresent(testDirectory -> {
                testDirectories.add(testDirectory);
            });
        });

        Map<String, Test> testsByFilename = getTests(testDirectories);

        testsByFilename.keySet().forEach(filename -> {
            String statusCode = getStatusCodeFromFilename(filename);
            HttpMethod httpMethod = getMethodFromFilename(filename);

            Test test = testsByFilename.get(filename);
            test.setStatusCode(statusCode);
            test.setMethod(httpMethod);

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

    private HttpMethod getMethodFromFilename(String filename) {
        HttpMethod httpMethod = HttpMethod.GET;
        if (filename.toLowerCase().startsWith("get")) {
            httpMethod = HttpMethod.GET;
        }
        if (filename.toLowerCase().startsWith("post")) {
            httpMethod = HttpMethod.POST;
        }
        if (filename.toLowerCase().startsWith("put")) {
            httpMethod = HttpMethod.PUT;
        }
        if (filename.toLowerCase().startsWith("patch")) {
            httpMethod = HttpMethod.PATCH;
        }
        if (filename.toLowerCase().startsWith("delete")) {
            httpMethod = HttpMethod.DELETE;
        }
        return httpMethod;
    }

    Map<String, Test> getTests(List<TestDirectory> testDirectories) {
        Map<String, Test> testByFilename = new HashMap<>();
        testDirectories.stream()
                       .map(TestDirectory::getTestFiles)
                       .forEach(testCase -> {
                           testCase.forEach(test -> {
                               try {
                                   String fileContent = new String(Files.readAllBytes(test.getPath()));
                                   if (isJson(test)) {
                                       JsonValue convertedObject = jsonConverter.convertToObject(fileContent);

                                       if (isTestFile(test)) {
                                           Test t = getTest(convertedObject);
                                           t.setName(test.getFileName());

                                           String[] splittedPath = test.getPath().toString().split("/");
                                           List<String> endpoint = getEndpoint(splittedPath);
                                           t.setEndpointParts(endpoint);


                                           String[] splittedByUnderscore = test.getFileName().split("_");
                                           if (splittedByUnderscore.length > 2) {
                                               t.getPathVariablesByName().put(endpoint.get(endpoint.size() - 1), splittedByUnderscore[1]);
                                           }




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

    private Test getTest(JsonValue fileContent) {
        Test.TestBuilder testBuilder = Test.builder();
        if (fileContent.containsKey(REQUEST)) {
            JsonValue requestWrapper = new JsonValue((ScriptObjectMirror) fileContent.get(REQUEST));
            JsonValue body = new JsonValue((ScriptObjectMirror) requestWrapper.get("body"));

            HttpHeaders httpHeaders = getHeaders(requestWrapper);
            testBuilder.requestHeaders(httpHeaders);

            Map<String, String> parameters = getParameters(requestWrapper);
            testBuilder.requestParams(parameters);

            testBuilder.request(body);
        }
        if (fileContent.containsKey(RESPONSE)) {
            testBuilder.response(new JsonValue((ScriptObjectMirror) fileContent.get(RESPONSE)));
        }
        if (fileContent.containsKey("description")) {

            testBuilder.description(((String) fileContent.get("description").toString())).build();
        }
        return testBuilder.pathVariablesByName(new HashMap<>()).build();
    }

    private HttpHeaders getHeaders(JsonValue requestWrapper) {
        HttpHeaders httpHeaders = new HttpHeaders();
        JsonValue rawHeaders = new JsonValue((ScriptObjectMirror) requestWrapper.get("headers"));
        try {
            rawHeaders.children().forEach(child -> {
                httpHeaders.add(child, (String) rawHeaders.get(child));
            });
        } catch (NullPointerException e) {
            log.warn("Test file does not contain headers section");
        }
        return httpHeaders;
    }

    private Map<String, String> getParameters(JsonValue requestWrapper) {
        Map<String, String> parameters = new HashMap<>();
        JsonValue rawParameters = new JsonValue((ScriptObjectMirror) requestWrapper.get("parameters"));
        rawParameters.children().forEach(child -> {
            parameters.put(child, (String) rawParameters.get(child));
        });
        return parameters;
    }

    private boolean isTestFile(TestFile f) {
        String asd = f.getFileName();
        String[] split = asd.split("[.]");
        boolean isTestFile = false;
        if (split.length == 2 && "json".equals(split[1])) {
            isTestFile = true;
        }
        return isTestFile;
    }

    private boolean isJson(TestFile f) {
        return f.getFileName().endsWith(JSON_SUFFIX);
    }

    private List<String> getEndpoint(String[] splittedPath) {
        List<String> endPointParts = new LinkedList<>();
        boolean found = false;
        for (int i = 0; i < splittedPath.length; i++) {
            if (!found && "test".equals(splittedPath[i]) && "rest".equals(splittedPath[i + 1])) {
                found = true;
                i = i + 1;
            } else if (found && i < splittedPath.length - 1) {
                endPointParts.add(splittedPath[i]);
            }
        }

        String joinedEndpointParts = endPointParts.stream()
                                      .collect(Collectors.joining("/"));
        return endPointParts;
    }
}


