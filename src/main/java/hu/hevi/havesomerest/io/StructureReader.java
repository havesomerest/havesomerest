package hu.hevi.havesomerest.io;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class StructureReader {

    public static final String TEST_DIR_PATH = "src/test/rest";

    public Map<Path, Optional<TestDirectory>> getStructure() throws IOException {
        Path rootPath = Paths.get(TEST_DIR_PATH).toAbsolutePath();

        Map<Path, Optional<TestDirectory.TestDirectoryBuilder>> filesInDirectory = new HashMap<>();
        Files.walk(rootPath)
             .filter(p -> !p.equals(rootPath))
             .sorted(this::orderDirFile)
             .forEach((path) -> {
                 if (!Files.isDirectory(path)) {
                     Path testFolderPath = getTestFolderPath(path);
                     Optional<TestDirectory.TestDirectoryBuilder> maybeTestDirectoryBuilder = filesInDirectory.get(testFolderPath);
                     Optional<TestDirectory.TestDirectoryBuilder> testDirectoryBuilder = Optional.ofNullable(maybeTestDirectoryBuilder)
                                                                                                 .orElse(Optional.of(new TestDirectory().toBuilder()));

                     testDirectoryBuilder.get().testFolder(getTestFolderPath(path));
                     testDirectoryBuilder.get().testFile(new TestFile(path));
                     filesInDirectory.put(path.getParent(), testDirectoryBuilder);
                 }
             });

        Map<Path, Optional<TestDirectory>> pathOptionalMap = new HashMap<>();
        filesInDirectory.keySet().forEach(key -> {
            Optional<TestDirectory> testDirectory = filesInDirectory.get(key).map(TestDirectory.TestDirectoryBuilder::build);
            pathOptionalMap.put(key, testDirectory);
        });

        return pathOptionalMap;
    }

    private int orderDirFile(Path a, Path b) {
        int returnValue = 0;
        if (!Files.isDirectory(a) && Files.isDirectory(b)) {
            returnValue = 1;
        } else if (Files.isDirectory(a) && !Files.isDirectory(b)) {
            returnValue = -1;
        }
        return returnValue;
    }


    private Path getTestFolderPath(Path path) {
        return path.getParent();
    }

}
