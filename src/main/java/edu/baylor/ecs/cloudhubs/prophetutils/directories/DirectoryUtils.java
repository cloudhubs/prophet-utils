package edu.baylor.ecs.cloudhubs.prophetutils.directories;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tech.sourced.enry.*;

public class DirectoryUtils {
    public static String[] getMsPaths(String root){
        File[] directories = filterDirectories(root);
        return Arrays.stream(directories).map(d -> d.getName()).toArray(String[]::new);
//        List<String> ignoreFolders = new ArrayList<>();
//        try {
//            Gson gson = new Gson();
//            //Reader ignoreReader = Files.newBufferedReader(Paths.get("ignoreFolders.json"));
//            InputStream inputStream = DirectoryUtils.class
//                    .getClassLoader().getResourceAsStream("ignoreFolders.json");
//            Reader ignoreReader = new InputStreamReader(inputStream);
//            ignoreFolders.addAll(Arrays.asList(gson.fromJson(ignoreReader, String[].class)));
//        } catch (Exception e) {
//            // log this somewhere
//            System.out.println("Failed to read folder exclusions, will parse all folders: " + e.getMessage());
//        }
//
//        File rootDir = new File(root);
////        try {
////            return (String[]) Files.list(new File(root).toPath())
////                    .limit(10)
////                    .map(n -> n.getFileName().toString())
////                    .collect(Collectors.toList()).toArray();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        return rootDir.list((current, name) -> new File(current, name).isDirectory() && !ignoreFolders.contains(name));
    }

    public static String[] getMsFullPaths(String root) {
        File[] directories = filterDirectories(root);
        return Arrays.stream(directories).map(d -> d.getPath()).toArray(String[]::new);
    }

    public static String getDirectoryNameFromPath(String dirPath) {
        File dir = new File(dirPath);
        return dir.getName();
    }

    public static boolean hasJava(String msFullPath) {
        boolean hasJava = false;
        // walk through all directories/files
        try (Stream<Path> paths = Files.walk(Paths.get(msFullPath))) {
            // find if there is any java file in this directory. If not, skip it
            hasJava = paths.anyMatch(p -> {
                File file = p.toFile();
                // only try to detect files
                if (!file.isDirectory()) {
                    // try only by extension first
                    Guess lang = Enry.getLanguageByExtension(file.getName());
                    if (lang.language.equals("Java")) {
                        if (lang.safe) {
                            return true;
                        } else {
                            try {
                                // try by content if needed
                                lang = Enry.getLanguageByContent(file.getName(), Files.readAllBytes(p));
                                if (lang.language.equals("Java") && lang.safe) {
                                    return true;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return false;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasJava;
    }

    private static File[] filterDirectories(String root) {
        List<String> ignoreFolders = getIgnoreFolders();

        File rootDir = new File(root);
        return rootDir.listFiles((current, name) -> new File(current, name).isDirectory() && !ignoreFolders.contains(name));
    }

    private static List<String> ignoreFolders = null;

    public static List<String> getIgnoreFolders() {
        if (ignoreFolders == null) {
            ignoreFolders = new ArrayList<>();
            try {
                Gson gson = new Gson();
                //Reader ignoreReader = Files.newBufferedReader(Paths.get("ignoreFolders.json"));
                InputStream inputStream = DirectoryUtils.class
                        .getClassLoader().getResourceAsStream("ignoreFolders.json");
                Reader ignoreReader = new InputStreamReader(inputStream);
                ignoreFolders.addAll(Arrays.asList(gson.fromJson(ignoreReader, String[].class)));
                System.out.println("Ignore Folders: " + ignoreFolders);
            } catch (Exception e) {
                // log this somewhere
                System.out.println("Failed to read folder exclusions, will parse all folders: " + e.getMessage());
            }
        }
        return ignoreFolders;
    }
}
