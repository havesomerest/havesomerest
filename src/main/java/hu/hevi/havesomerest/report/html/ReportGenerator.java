package hu.hevi.havesomerest.report.html;

import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;

import java.io.IOException;
import java.util.Map;

/**
 * Created by hevi on 30/07/16.
 */
public interface ReportGenerator {
    void generateReport(Map<Test, TestResult> results) throws IOException;
}
