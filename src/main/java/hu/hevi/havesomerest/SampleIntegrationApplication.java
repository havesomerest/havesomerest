package hu.hevi.havesomerest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude={org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class})
public class SampleIntegrationApplication {

	public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(SampleIntegrationApplication.class, args);
        ApplicationRunner applicationRunner = (ApplicationRunner) ctx.getBean(ApplicationRunner.class);
        applicationRunner.run();
    }

}
