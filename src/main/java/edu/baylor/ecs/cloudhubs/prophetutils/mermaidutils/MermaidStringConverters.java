package edu.baylor.ecs.cloudhubs.prophetutils.mermaidutils;

import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.ms.MsMermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.EntityGraphAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.MsGraphAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.directories.DirectoryUtils;
import edu.baylor.ecs.cloudhubs.prophetutils.mscontext.SourceParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static edu.baylor.ecs.cloudhubs.prophetutils.ProphetUtilsFacade.getBoundedContext;

public class MermaidStringConverters {
    public static String getBoundedContextMermaidString(BoundedContext boundedContext) {
        MermaidGraph mermaidGraph = EntityGraphAdapter.getMermaidGraph(boundedContext);
        List<String> htmlTemplate = mermaidGraph.getHtmlLines();
        StringBuilder sb = new StringBuilder();
        for (String line : htmlTemplate) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * Creates a mermaid from the results of the RAD tool
     *
     * @author Vincent Bushong
     * @param model An MsModel created by RAD
     * @return Mermaid markdown of the context map of the API communication
     */
    public static String getMsModelMermaidString(MsModel model) {
        MsMermaidGraph graph = MsGraphAdapter.getMermaidGraphFromMsModel(model);

        // get the mermaid markdown lines
        List<String> htmlTemplate = graph.getHtmlLines();
        StringBuilder sb = new StringBuilder();
        for (String line : htmlTemplate) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
