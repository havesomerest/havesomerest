package hu.hevi.havesomerest.integration;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@Log
class ApplicationRunner {

    void run() {
        try {
            Path rootPath = Paths.get("src/test/rest").toAbsolutePath();

            Map<Path, Optional<EndPoint.EndPointBuilder>> filesInDirectory = new HashMap<>();
            Files.walk(rootPath)
                 .filter(p -> !p.equals(rootPath))
                 .sorted((a, b) -> orderDirFile(a, b))
                 .forEach((path) -> {
                    //// GET THE FILES IN A DIRECTORY

                     if (!Files.isDirectory(path)) {
                         Optional<EndPoint.EndPointBuilder> endPointBuilder = Optional.ofNullable(filesInDirectory.get(getEndPointPath(path)))
                                                               .orElse(Optional.of(new EndPoint().toBuilder()));

                         endPointBuilder.get().path(getEndPointPath(path));


                         endPointBuilder.get().resource(new Resource(path));
                         filesInDirectory.put(path.getParent(), endPointBuilder);
                     }



                     ////////
                     absolutePath(path, rootPath).forEach(p -> {
                         System.out.println(p.toString());
                     });
                     System.out.println((Files.isDirectory(path) ? "d" : "f") + " " + path.toAbsolutePath().toString());
                 });

            for (Map.Entry<Path, Optional<EndPoint.EndPointBuilder>> entry : filesInDirectory.entrySet()) {
                EndPoint.EndPointBuilder endPointBuilder = entry.getValue().get();
                EndPoint endPoint = endPointBuilder.build();
                log.info(endPoint.getPath().toString());
                endPoint.getResources().forEach(f -> {
                    log.info(f.getPath().toString());
                    f.ifPost(() -> {
                        System.out.println("POOOOOST");
                    });
                });
            }
//
//            Files.newDirectoryStream(rootPath).forEach((directoryPath) -> {
//                Path path = Paths.get(directoryPath.toString());
//                System.out.println("Current relative path is: " + path.getFileName().toString());
//            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getEndPointPath(Path path) {
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
