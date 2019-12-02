package edu.baylor.ecs.cloudhubs.prophetutils.adapter;

import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;

import java.io.*;
import java.util.List;

public class HtmlTemplateAdapter {

    public static void saveGraphToFile(MermaidGraph mermaidGraph){
        List<String> start = FileManager.readHtmlTemplatesToList("src/main/resources/header.html");
        List<String> end = FileManager.readHtmlTemplatesToList("src/main/resources/footer.html");
        try{
            //write it
            BufferedWriter bw = new BufferedWriter(new FileWriter("index.html"));
            PrintWriter out = new PrintWriter(bw);

            for (String s: start
                 ) {
                out.println(s);
            }

            for (String s: mermaidGraph.getHtmlLines()
                 ) {
                out.println(s);
            }

            for (String s: end){
                out.println(s);
            }

            out.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public static List<String> getHtmlTemplateList(MermaidGraph mermaidGraph) {
        List<String> start = FileManager.readHtmlTemplatesToList("src/main/resources/header.html");
        List<String> graph = mermaidGraph.getHtmlLines();
        List<String> end = FileManager.readHtmlTemplatesToList("src/main/resources/footer.html");
        start.addAll(graph);
        start.addAll(end);
        return start;
    }




}
