package hu.hevi.havesomerest.report.html.impl.thymeleaf;

import hu.hevi.havesomerest.report.html.ReportGenerator;
import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            result.setRequestJson(key.getRequest()
                                     .toString(2));
            result.setActualResponseJson(results.get(key)
                                                .getResponseBody()
                                                .toString(2));
            result.setExpectedResponseJson(key.getResponse()
                                              .toString(2));
            convertedResults.add(result);
        });
        return convertedResults;
    }
}
