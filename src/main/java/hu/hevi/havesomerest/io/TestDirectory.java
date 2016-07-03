package hu.hevi.havesomerest.io;

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
    private List<TestFile> testFiles = new ArrayList<>();

}
