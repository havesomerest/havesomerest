package hu.hevi.havesomerest;

import lombok.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TestDirectory {

    private Path testFolder;
    @Singular
    private List<TestCase> testCases = new ArrayList<>();

}