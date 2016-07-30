package hu.hevi.havesomerest;

import lombok.Data;

@Data
public class ReportFileTemplateTestResult {

    private String requestJson;
    private String expectedResponseJson;
    private String actualResponseJson;

}
