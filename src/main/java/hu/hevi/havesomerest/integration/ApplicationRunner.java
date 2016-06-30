package hu.hevi.havesomerest.integration;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@Slf4j
class ApplicationRunner {

    public static final String RESPONSE = "response";
    public static final String REQUEST = "request";
    @Autowired
    private JsBasedJsonConverter jsonConverter;

    void run() {
        try {
            Path rootPath = Paths.get("src/test/rest").toAbsolutePath();

            Map<Path, Optional<TestDirectory.TestDirectoryBuilder>> filesInDirectory = new HashMap<>();
            Files.walk(rootPath)
                 .filter(p -> !p.equals(rootPath))
                 .sorted((a, b) -> orderDirFile(a, b))
                 .forEach((path) -> {
                    //// GET THE FILES IN A DIRECTORY

                     if (!Files.isDirectory(path)) {
                         Optional<TestDirectory.TestDirectoryBuilder> testDirectoryBuilder = Optional.ofNullable(filesInDirectory.get(getTestFolderPath(path)))
                                                                                           .orElse(Optional.of(new TestDirectory().toBuilder()));

                         testDirectoryBuilder.get().testFolder(getTestFolderPath(path));
                         testDirectoryBuilder.get().file(new TestCase(path));
                         filesInDirectory.put(path.getParent(), testDirectoryBuilder);
                     }

                     ////////
                     absolutePath(path, rootPath).forEach(p -> {
                         System.out.println(p.toString());
                     });
                     System.out.println((Files.isDirectory(path) ? "d" : "f") + " " + path.toAbsolutePath().toString());
                 });








            //for (Map.Entry<Path, Optional<TestDirectory.TestDirectoryBuilder>> entry : filesInDirectory.entrySet()) {
            filesInDirectory.entrySet().forEach((entry) -> {
                TestDirectory.TestDirectoryBuilder endPointBuilder = entry.getValue().get();
                TestDirectory testDirectory = endPointBuilder.build();
                //log.info(testDirectory.getTestFolder().toString());
                testDirectory.getFiles().forEach(f -> {
                    //log.info(f.getPath().toString());
                    f.ifPost(() -> {
                        System.out.println("POOOOOST");
                    });

                    try {
                        FileInputStream fis = new FileInputStream(f.getPath().toFile());

                        String s = new String(Files.readAllBytes(f.getPath()));
                        System.out.println(s);


                        if (f.getFileName().endsWith(".json")) {
                            ScriptObjectMirror convert = jsonConverter.convertToObject(s);

                            Test.TestBuilder testBuilder = Test.builder();
                            if (convert.containsKey(REQUEST)) {
                                testBuilder.request((ScriptObjectMirror) convert.get(REQUEST)).build();
                            }
                            if (convert.containsKey(RESPONSE)) {
                                testBuilder.request((ScriptObjectMirror) convert.get(RESPONSE)).build();
                            }

                            System.out.println("COOOONVERT: " + convert.keySet());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                });
            });


//
//            Files.newDirectoryStream(rootPath).forEach((directoryPath) -> {
//                Path testFolder = Paths.get(directoryPath.toString());
//                System.out.println("Current relative testFolder is: " + testFolder.getFileName().toString());
//            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getTestFolderPath(Path path) {
        return path.getParent();
    }

    private List<Path> absolutePath(Path path, Path rootPath) {
        List<Path> paths1 = null;
        if (!path.equals(rootPath)) {
            paths1 = absolutePath(path.getParent(), rootPath);
        }
        paths1 = new LinkedList<>();
        paths1.add(path);
        return paths1;
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

    private String getRestApiPath(Path restTestPath, Path asdf) {
        return asdf.toString().substring(restTestPath.toString().length());
    }

}
