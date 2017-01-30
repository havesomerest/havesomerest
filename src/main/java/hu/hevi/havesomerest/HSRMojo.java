package hu.hevi.havesomerest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Mojo(name = "test")
public class HSRMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        ConfigurableApplicationContext ctx = SpringApplication.run(SampleIntegrationApplication.class);
        ApplicationRunner applicationRunner = (ApplicationRunner) ctx.getBean(ApplicationRunner.class);
        applicationRunner.run();
    }

}
