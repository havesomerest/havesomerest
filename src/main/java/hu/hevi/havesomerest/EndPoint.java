package hu.hevi.havesomerest;

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
    private List<TestCase> testCases = new ArrayList<>();

}
