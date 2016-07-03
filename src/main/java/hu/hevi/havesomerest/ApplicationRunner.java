package hu.hevi.havesomerest;

import hu.hevi.havesomerest.converter.ToTestConverter;
import hu.hevi.havesomerest.io.StructureReader;
import hu.hevi.havesomerest.io.TestDirectory;
import hu.hevi.havesomerest.test.Test;
import hu.hevi.havesomerest.test.TestRunner;
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
    @Autowired
    private TestRunner testRunner;

    void run() {
        try {

            Map<Path, Optional<TestDirectory.TestDirectoryBuilder>> filesByDirectory = structureReader.getStructure();
            Set<Test> tests = toTestConverter.convert(filesByDirectory);
            testRunner.runTests(tests);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
