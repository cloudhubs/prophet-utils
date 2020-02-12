package edu.baylor.ecs.cloudhubs.prophetutils.directories;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryUtils {
    public static String[] getMsPaths(String root){
        List<String> ignoreFolders = new ArrayList<>();
        try {
            Gson gson = new Gson();
            //Reader ignoreReader = Files.newBufferedReader(Paths.get("ignoreFolders.json"));
            InputStream inputStream = DirectoryUtils.class
                    .getClassLoader().getResourceAsStream("ignoreFolders.json");
            Reader ignoreReader = new InputStreamReader(inputStream);
            ignoreFolders.addAll(Arrays.asList(gson.fromJson(ignoreReader, String[].class)));
        } catch (Exception e) {
            // log this somewhere
            System.out.println("Failed to read folder exclusions, will parse all folders: " + e.getMessage());
        }

        File rootDir = new File(root);
//        try {
//            return (String[]) Files.list(new File(root).toPath())
//                    .limit(10)
//                    .map(n -> n.getFileName().toString())
//                    .collect(Collectors.toList()).toArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return rootDir.list((current, name) -> new File(current, name).isDirectory() && !ignoreFolders.contains(name));
    }
}
