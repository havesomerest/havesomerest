package hu.hevi.havesomerest.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:test.properties")
@Data
public class TestProperties {

    @Value("${test.server.host}")
    private String testServerHost;
}
