package edu.baylor.ecs.cloudhubs.prophetutils.filemanager;

import com.google.gson.Gson;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static void writeToFile(SystemContext systemContext){
        try {
            Gson gson = new Gson();
            String json = gson.toJson(systemContext);
            BufferedWriter writer = new BufferedWriter(new FileWriter("systemContext.json"));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
