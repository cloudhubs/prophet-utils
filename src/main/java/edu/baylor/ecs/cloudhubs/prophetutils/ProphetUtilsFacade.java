package edu.baylor.ecs.cloudhubs.prophetutils;

import edu.baylor.ecs.cloudhubs.prophetdto.app.MicroserviceResult;
import edu.baylor.ecs.cloudhubs.prophetdto.app.ProphetAppData;
import edu.baylor.ecs.cloudhubs.prophetdto.app.ProphetAppRequest;
import edu.baylor.ecs.cloudhubs.prophetdto.app.ProphetResponse;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.Communication;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.ContextMap;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.Edge;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.Node;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.ms.MsMermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.EntityContextAdapter;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.EntityGraphAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.HtmlTemplateAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.MsGraphAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.bounded.SimpleBoundedUtils;
import edu.baylor.ecs.cloudhubs.prophetutils.directories.DirectoryUtils;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import edu.baylor.ecs.cloudhubs.prophetutils.jparser.JParserUtils;
import edu.baylor.ecs.cloudhubs.prophetutils.mscontext.SourceParser;
import edu.baylor.ecs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.prophet.bounded.context.utils.BoundedContextUtils;
import edu.baylor.ecs.prophet.bounded.context.utils.impl.BoundedContextUtilsImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProphetUtilsFacade {

    /**
     * ToDo: change return object to MsModel
     * @param path - file system path to micro services
     */
    public static void getMsModelSource(String path){

    }


    /**
     * ToDo: change return object to MsModel
     * @param path - file system path to micro services
     */
    public static void getMsModelByte(String path) {

    }

    /**
     * Generates App Data from microservice system
     * @param path
     * @return ProphetAppData
     */
    public static ProphetAppData getProphetAppData(String path) throws IOException {
        ProphetAppData response = new ProphetAppData();

        // Set the globals: Project name and context map for bounded context and microservice communication
        JParserUtils jParserUtils = JParserUtils.getInstance();
        response.setProjectName(jParserUtils.createAnalysisContextFromDirectory(path).getRootPath());
        response.setContextMap(ProphetUtilsFacade.getContextMap(path));
        response.setCommunication(getRestContextMap(path));

        // Get each microservice's bounded context
        String[] msPaths = DirectoryUtils.getMsPaths(path);
        List<MicroserviceResult> msResults = new ArrayList<>();
        for (String msPath : msPaths) {
            MicroserviceResult msResult = new MicroserviceResult();
            BoundedContext boundedContext = ProphetUtilsFacade.getBoundedContext(path, new String[]{msPath});
            msResult.setName(msPath);
            msResult.setBoundedContext(boundedContext);
            msResults.add(msResult);
        }
        response.setMicroservices(msResults);

        return response;
    }


    /**
     * Generates Bounded Context From Source Code of Microservice System
     * @param path
     * @return BoundedContext
     */
    public static BoundedContext getBoundedContext(String path, String[] msPaths) {
        BoundedContextUtils boundedContextUtils = new BoundedContextUtilsImpl();
        SystemContext systemContext = ProphetUtilsFacade.getEntityContext(path, msPaths);
        FileManager.writeToFile(systemContext);
        //BoundedContext boundedContext = SimpleBoundedUtils.getBoundedContext(systemContext);
        BoundedContext boundedContext = boundedContextUtils.createBoundedContext(systemContext);
        return boundedContext;
    }


    /**
     * Generates entity context of microservice system
     * @param path to root folder
     * @param msPaths to microservices
     * @return entity context
     */
    public static SystemContext getEntityContext(String path, String[] msPaths){
        JParserUtils jParserUtils = JParserUtils.getInstance();
        AnalysisContext analysisContext = jParserUtils.createAnalysisContextFromDirectory(path);
        SystemContext systemContext = EntityContextAdapter.getSystemContext(analysisContext, msPaths);
        return systemContext;
    }


    public static List<String> createHtmlTemplate(String path, String[] msPaths){
        BoundedContext boundedContext = getBoundedContext(path, msPaths);
        MermaidGraph mermaidGraph = EntityGraphAdapter.getMermaidGraph(boundedContext);
        return HtmlTemplateAdapter.getHtmlTemplateList(mermaidGraph);
    }

    public static MermaidGraph getMermaidGraph(String path, String[] msPaths){
        BoundedContext boundedContext = getBoundedContext(path, msPaths);
        return EntityGraphAdapter.getMermaidGraph(boundedContext);
    }

    public static ProphetResponse getProphetResponse(String path){
        String[] htmlTemplate = (String[]) createHtmlTemplate(path, DirectoryUtils.getMsPaths(path)).toArray();
        ProphetResponse prophetResponse = new ProphetResponse();
        prophetResponse.setContextMap(htmlTemplate);
        return prophetResponse;
    }

    public static Communication getCommunication(String path){
        Communication communication = new Communication();
        Edge[] edges = new Edge[3];
        Node[] nodes = new Node[3];
        communication.setEdges(edges);
        communication.setNodes(nodes);
        return communication;
    }

    public static ContextMap getContextMap(String path) {
        ContextMap contextMap = new ContextMap();
        BoundedContext boundedContext = getBoundedContext(path, DirectoryUtils.getMsPaths(path));
        MermaidGraph mermaidGraph = EntityGraphAdapter.getMermaidGraph(boundedContext);


        List<String> htmlTemplate = mermaidGraph.getHtmlLines();
        String[] strings = new String[htmlTemplate.size()];
        for (int i = 0; i < htmlTemplate.size(); i++){
            strings[i] = htmlTemplate.get(i);
        }
        contextMap.setMarkdownStrings(strings);
        return contextMap;
    }

    /**
     * Creates a context map from the results of the Rest API Discovery tool
     *
     * @author Vincent Bushong
     * @param path to project root
     * @return ContextMap of the API communication
     */
    public static ContextMap getRestContextMap(String path) throws IOException {
        SourceParser parser = new SourceParser();
        // get the full paths to the microservice directories
        List<String> msPaths = Arrays.asList(DirectoryUtils.getMsPaths(path)).stream().map(ms -> path + "/" + ms).collect(Collectors.toList());

        // get the microservice communication model and convert to mermaid graph representation
        MsModel model = parser.createMsModel(msPaths);
        MsMermaidGraph graph = MsGraphAdapter.getMermaidGraphFromMsModel(model);

        // get the mermaid markdown lines
        ContextMap contextMap = new ContextMap();
        String[] markdownLines = new String[graph.getHtmlLines().size()];
        markdownLines = graph.getHtmlLines().toArray(markdownLines);
        contextMap.setMarkdownStrings(markdownLines);

        return contextMap;
    }

}
