package hu.hevi.havesomerest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
class ApplicationRunner {

    @Autowired
    private StructureReader structureReader;
    @Autowired
    private ToTestConverter toTestConverter;

    void run() {
        try {

            Map<Path, Optional<TestDirectory.TestDirectoryBuilder>> filesInDirectory = structureReader.getStructure();
            Set<Test> tests = toTestConverter.convert(filesInDirectory);

            tests.forEach(test -> {
                System.out.println(test.getRequest().entrySet().toString());
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
