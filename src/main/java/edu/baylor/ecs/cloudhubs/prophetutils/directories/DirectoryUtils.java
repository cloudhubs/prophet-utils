package edu.baylor.ecs.cloudhubs.prophetutils.directories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class DirectoryUtils {
    public static String[] getMsPaths(String root){
        File file = new File(root);
//        try {
//            return (String[]) Files.list(new File(root).toPath())
//                    .limit(10)
//                    .map(n -> n.getFileName().toString())
//                    .collect(Collectors.toList()).toArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return file.list((current, name) -> new File(current, name).isDirectory());
    }
}
