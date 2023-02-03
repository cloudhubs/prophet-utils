package edu.baylor.ecs.cloudhubs.prophetutils.directories;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectoryUtilsTest {
//    @TempDir
//    Path tempDir;
//
//    @Test
//    public void getMsPathsTest() throws IOException {
//        String tempPath = tempDir.toString();
//        Path rootFolder = Paths.get(tempPath, "testroot");
//        Path msOne = Paths.get(tempPath, "testroot", "A");
//        Path msTwo = Paths.get(tempPath, "testroot", "B");
//        Path msThree = Paths.get(tempPath, "testroot", "C");
//        Path git = Paths.get(tempPath, "testroot", ".git");
//        Path mvn = Paths.get(tempPath, "testroot", ".mvn");
//        Path github = Paths.get(tempPath, "testroot", ".github");
//        Path target = Paths.get(tempPath, "testroot", "target");
//        Files.createDirectories(rootFolder);
//        Files.createDirectories(msOne);
//        Files.createDirectories(msTwo);
//        Files.createDirectories(msThree);
//        Files.createDirectories(git);
//        Files.createDirectories(mvn);
//        Files.createDirectories(github);
//        Files.createDirectories(target);
//
//
//        List<String> realFolders = Arrays.asList("A", "B", "C");
//
//        String root = rootFolder.toString();
//        List<String> msPaths = Arrays.asList(DirectoryUtils.getMsPaths(root));
//
//        assert(msPaths.size() == 3);
//        assert(msPaths.containsAll(realFolders));
//    }
}
