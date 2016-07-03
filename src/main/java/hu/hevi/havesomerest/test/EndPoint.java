package hu.hevi.havesomerest.test;

import lombok.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EndPoint {

    private URI path;
    @Singular
    private List<Test> tests = new ArrayList<>();

}
