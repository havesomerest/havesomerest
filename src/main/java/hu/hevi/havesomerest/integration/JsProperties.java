package hu.hevi.havesomerest.integration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:js.properties")
@Data
public class JsProperties {

    @Value("${js.converter.location}")
    private String location;
}
