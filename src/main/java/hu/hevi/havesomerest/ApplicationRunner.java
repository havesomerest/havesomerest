package hu.hevi.havesomerest;

import hu.hevi.havesomerest.common.EndPointNameBuilder;
import hu.hevi.havesomerest.converter.ToTestConverter;
import hu.hevi.havesomerest.html.ReportGenerator;
import hu.hevi.havesomerest.io.StructureReader;
import hu.hevi.havesomerest.io.TestDirectory;
import hu.hevi.havesomerest.json.ResultJsonGenerator;
import hu.hevi.havesomerest.test.ResultType;
import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestResult;
import hu.hevi.havesomerest.test.TestRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ViewResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
class ApplicationRunner {

    @Autowired
    private ApplicationContext ctx;
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
    @Autowired
    private ViewResolver viewResolver;
    @Autowired
    private ReportGenerator reportGenerator;
    @Autowired
    private ResultJsonGenerator resultJsonGenerator;

    void run() throws MojoFailureException {
        try {
            Map<Path, Optional<TestDirectory>> filesByDirectory = structureReader.getStructure();
            Map<Test, String> tests = toTestConverter.convert(filesByDirectory);
            Map<Test, TestResult> results = testRunner.runTests(tests.keySet());

            log.info("---------------\n");

            long passedCount = results.values()
                                      .stream()
                                      .filter(p -> ResultType.PASSED.equals(p.getResultType()))
                                      .count();

            long failedTestCount = results.keySet().size() - passedCount;

            log.info(MessageFormat.format("{0} of {1} test PASSED, {2} FAILED\n",
                                          passedCount,
                                          results.keySet().size(),
                                          failedTestCount));

            log.info("---------------\n");

//            resultJsonGenerator.generateResult(tests, results);
            //reportGenerator.generateReport(results);

            log.info(MessageFormat.format("Finished at: {0}", LocalDateTime.now()));

            if (failedTestCount > 0l) {
                throw new MojoFailureException(failedTestCount + " test failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ((ConfigurableApplicationContext) ctx).close();
        }
    }
}
