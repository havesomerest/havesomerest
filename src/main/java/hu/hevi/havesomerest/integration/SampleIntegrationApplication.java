package hu.hevi.havesomerest.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class SampleIntegrationApplication {

	public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(SampleIntegrationApplication.class, args);
        ApplicationRunner applicationRunner = (ApplicationRunner) ctx.getBean(ApplicationRunner.class);
        applicationRunner.run();
    }

}
