package edu.baylor.ecs.cloudhubs.prophetutils.filemanager;

import com.google.gson.Gson;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    public static String readHtmlTemplates(String path){
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String currentLine = reader.readLine();
            while (currentLine != null) {
                builder.append(currentLine);
                currentLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static List<String> readHtmlTemplatesToList(String path){
        List<String> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String currentLine = reader.readLine();
            while (currentLine != null) {
                list.add(currentLine);
                currentLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
