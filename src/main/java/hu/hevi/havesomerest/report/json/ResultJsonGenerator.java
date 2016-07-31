package hu.hevi.havesomerest.report.json;

import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public interface ResultJsonGenerator {
    void generateResult(Map<Test, JSONObject> tests, Map<Test, TestResult> results) throws IOException;
}
