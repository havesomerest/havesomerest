package hu.hevi.havesomerest.report.html.impl.thymeleaf;

import lombok.Data;

@Data
public class ReportFileTemplateTestResult {

    private String requestJson;
    private String expectedResponseJson;
    private String actualResponseJson;

}
