package hu.hevi.havesomerest.report.html.impl.thymeleaf;

import hu.hevi.havesomerest.report.html.ReportGenerator;
import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Component
public class ThymeleafBasedReportGenerator implements ReportGenerator {

    public static final String INDEX_HTML_PATH = "target/havesomerest/index.html";
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void generateReport(Map<Test, TestResult> results) throws IOException {
        log.info("Generating report HTML...\n");

        List<ReportFileTemplateTestResult> convertedResults = getReportFileTemplateTestResults(results);

        final Context ctx = new Context(Locale.UK);
        ctx.setVariable("results", convertedResults);

        final String htmlContent = this.templateEngine.process("testResults.html", ctx);

        Path path = Paths.get(INDEX_HTML_PATH);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(htmlContent);
        }
    }

    private List<ReportFileTemplateTestResult> getReportFileTemplateTestResults(Map<Test, TestResult> results) {
        List<ReportFileTemplateTestResult> convertedResults = new LinkedList<>();
        results.keySet().forEach(key -> {
            ReportFileTemplateTestResult result = new ReportFileTemplateTestResult();
            if (key.hasRequest()) {
                JSONObject requestJson = new JSONObject(key.getRequest());
                result.setRequestJson(requestJson.toString(2));
            }


            Optional<JSONObject> actualResponseJson = Optional.ofNullable(results.get(key)
                                                                                 .getResponseBody());
            actualResponseJson.ifPresent(r -> result.setActualResponseJson(actualResponseJson.get().toString(2)));

            if (key.hasResponse()) {
                JSONObject responseJson = new JSONObject(key.getResponse());
                result.setExpectedResponseJson(responseJson.toString(2));
            }
            convertedResults.add(result);
        });
        return convertedResults;
    }
}
