package hu.hevi.havesomerest.integration;

import lombok.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EndPoint {

    private Path path;
    @Singular
    private List<Resource> resources = new ArrayList<>();

}
