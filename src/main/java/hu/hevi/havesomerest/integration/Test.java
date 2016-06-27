package hu.hevi.havesomerest.integration;

import lombok.Value;

import java.nio.file.Path;

@Value
public class Test {

    private Path testFile;
    private Path dbFile;
    private Path assertFile;

    public Test(Test test) {
        this.testFile = test.testFile;
        this.dbFile = test.dbFile;
        this.assertFile = test.assertFile;
    }
}
