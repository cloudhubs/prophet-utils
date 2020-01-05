package edu.baylor.ecs.cloudhubs.prophetutils.directories;

import java.io.File;

public class DirectoryUtils {
    public static String[] getMsPaths(String root){
        File file = new File(root);
        return file.list((current, name) -> new File(current, name).isDirectory());
    }
}
