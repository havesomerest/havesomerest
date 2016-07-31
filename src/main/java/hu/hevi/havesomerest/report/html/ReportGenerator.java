package hu.hevi.havesomerest.report.html;

import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;

import java.io.IOException;
import java.util.Map;

public interface ReportGenerator {
    void generateReport(Map<Test, TestResult> results) throws IOException;
}
