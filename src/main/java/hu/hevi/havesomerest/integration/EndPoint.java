package hu.hevi.havesomerest.integration;

import lombok.Data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
public class EndPoint {

    private Path path;
    private List<Test> tests = new ArrayList<>();
}
