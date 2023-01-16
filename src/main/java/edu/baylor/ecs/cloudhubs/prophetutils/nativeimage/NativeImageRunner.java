package edu.baylor.ecs.cloudhubs.prophetutils.nativeimage;

import com.google.gson.Gson;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Module;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NativeImageRunner {

    private static final String NI_BASE = "/Users/dkozak/Projects/graal/sdk/mxbuild/darwin-amd64/GRAALVM_8F70D52881_JAVA17/graalvm-8f70d52881-java17-23.0.0-dev/Contents/Home";

    private static final String NI_CMD = NI_BASE + "/bin/native-image";

    private final String microservicePath;
    private final String classpath;
    private final String outputJson;

    private final MicroserviceInfo info;

    public NativeImageRunner(MicroserviceInfo info) {
        this.info = info;
        this.microservicePath = info.getBaseDir() + File.separator + info.getMicroserviceName();
        this.classpath = microservicePath + "/BOOT-INF/classes" + ":" + microservicePath + "/BOOT-INF/lib/*";
        this.outputJson = info.getMicroserviceName() + ".json";
    }

    public Module runProphetPlugin() {
        executeNativeImage();
        return parseOutputFile();
    }

    private Module parseOutputFile() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(outputJson)) {
            return gson.fromJson(reader, Module.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeNativeImage() {
        List<String> cmd = prepareCommand();
        System.out.println(String.join(" ", cmd));
        try {
            Process process = new ProcessBuilder()
                    .command(cmd)
                    .inheritIO()
                    .start();
            int res = process.waitFor();
            if (res != 0) {
                System.err.println("Failed to execute command.");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private List<String> prepareCommand() {
        List<String> cmd = new ArrayList<>();
        cmd.add(NI_CMD);
        cmd.add("-cp");
        cmd.add(classpath);
        cmd.add("-H:+ProphetPlugin");
        cmd.add("-H:ProphetModuleName=" + this.info.getMicroserviceName());
        cmd.add("-H:ProphetBasePackage=" + this.info.getBasePackage());
        cmd.add("-H:ProphetOutputFile=" + this.outputJson);
        cmd.add("dummy-main");
        return cmd;
    }
}
