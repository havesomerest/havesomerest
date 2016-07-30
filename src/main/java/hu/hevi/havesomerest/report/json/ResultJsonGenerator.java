package hu.hevi.havesomerest.report.json;

import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by hevi on 30/07/16.
 */
public interface ResultJsonGenerator {
    void generateResult(Map<Test, JSONObject> tests, Map<Test, TestResult> results) throws IOException;
}
