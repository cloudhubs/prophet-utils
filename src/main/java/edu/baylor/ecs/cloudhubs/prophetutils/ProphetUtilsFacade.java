package edu.baylor.ecs.cloudhubs.prophetutils;

import edu.baylor.ecs.cloudhubs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.cloudhubs.prophetdto.app.*;
import edu.baylor.ecs.cloudhubs.prophetdto.app.utilsapp.GitReq;
import edu.baylor.ecs.cloudhubs.prophetdto.app.utilsapp.RepoReq;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.Communication;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.ContextMap;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.Edge;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.Node;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.EntityContextAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.EntityGraphAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.HtmlTemplateAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.directories.DirectoryUtils;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import edu.baylor.ecs.cloudhubs.prophetutils.jparser.JParserUtils;
import edu.baylor.ecs.cloudhubs.prophetutils.mermaidutils.MermaidStringConverters;
import edu.baylor.ecs.cloudhubs.prophetutils.mscontext.SourceParser;
import edu.baylor.ecs.prophet.bounded.context.utils.BoundedContextUtils;
import edu.baylor.ecs.prophet.bounded.context.utils.impl.BoundedContextUtilsImpl;

import java.io.IOException;
import java.util.*;

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

    public static ProphetAppData getProphetAppData(GitReq request) throws IOException {
        List<String> msFullPaths = new ArrayList<>();
        for (RepoReq repo : request.getRepositories()) {
            if (repo.isMonolith()) {
                msFullPaths.add(repo.getPath());
            } else {
                msFullPaths.addAll(Arrays.asList(DirectoryUtils.getMsFullPaths(repo.getPath())));
            }
        }

        ProphetAppData response = new ProphetAppData();
        ProphetAppGlobal global = new ProphetAppGlobal();
        global.setProjectName(request.getSystemName());

        // get the context and MsModel of the project
        BoundedContext globalContext = getBoundedContext(msFullPaths);
        MsModel msModel = getMsModel(msFullPaths);

        // get the mermaid string representations of the context and model
        global.setContextMap(MermaidStringConverters.getBoundedContextMermaidString(globalContext));
        global.setCommunication(MermaidStringConverters.getMsModelMermaidString(msModel));

        response.setGlobal(global);

        List<MicroserviceResult> msResults = getMsBoundedContexts(msFullPaths);
        response.setMs(msResults);

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
//        BoundedContext boundedContext = SimpleBoundedUtils.getBoundedContext(systemContext);
        BoundedContext boundedContext = boundedContextUtils.createBoundedContext(systemContext);
        return boundedContext;
    }

    /**
     * Generates Bounded Context From multirepo microservice project
     * @param msFullPaths
     * @return BoundedContext
     */
    public static BoundedContext getBoundedContext(List<String> msFullPaths) {
        BoundedContextUtils boundedContextUtils = new BoundedContextUtilsImpl();
        SystemContext systemContext = ProphetUtilsFacade.getEntityContext(msFullPaths);
        FileManager.writeToFile(systemContext);
//        BoundedContext boundedContext = SimpleBoundedUtils.getBoundedContext(systemContext);
        BoundedContext boundedContext = boundedContextUtils.createBoundedContext(systemContext);
        return boundedContext;
    }

    /**
     * Generates a bounded context for each microservice
     * @param msFullPaths
     * @return List<MicroserviceResult>
     */
    public static List<MicroserviceResult> getMsBoundedContexts(List<String> msFullPaths) {
        List<MicroserviceResult> msResults = new ArrayList<>();
        for (String msPath : msFullPaths) {
            MicroserviceResult msResult = new MicroserviceResult();

            // get the bounded context from this single microservice
            List<String> singleMsPathList = new ArrayList<>(Arrays.asList(msPath));
            BoundedContext boundedContext = ProphetUtilsFacade.getBoundedContext(singleMsPathList);

            if (boundedContext.getBoundedContextEntities().size() != 0) {
                msResult.setName(DirectoryUtils.getDirectoryNameFromPath(msPath));

                // get the mermaid representation of the bounded context
                msResult.setBoundedContext(MermaidStringConverters.getBoundedContextMermaidString(boundedContext));
                msResults.add(msResult);
            }
        }
        return msResults;
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

    /**
     * Generates entity context of microservice system across multiple repos
     * @param msFullPaths to microservices
     * @return entity context
     */
    public static SystemContext getEntityContext(List<String> msFullPaths){
        JParserUtils jParserUtils = JParserUtils.getInstance();
        AnalysisContext analysisContext = jParserUtils.createAnalysisContextFromMultipleDirectories(msFullPaths);
        SystemContext systemContext = EntityContextAdapter.getSystemContext(analysisContext, msFullPaths.toArray(new String[msFullPaths.size()]));
        return systemContext;
    }


    public static List<String> createHtmlTemplate(String path, String[] msPaths){
        BoundedContext boundedContext = getBoundedContext(path, msPaths);
        MermaidGraph mermaidGraph = EntityGraphAdapter.getMermaidGraph(boundedContext);
        return HtmlTemplateAdapter.getHtmlTemplateList(mermaidGraph);
    }

    public static List<String> createHtmlTemplate(List<String> msFullPaths){
        BoundedContext boundedContext = getBoundedContext(msFullPaths);
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
        Set<Edge> edges = new HashSet<>();
        Set<Node> nodes = new HashSet<>();
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
     * Uses the RAD source analyzer to get an MsModel from a directory
     * @param path Path to the ms roots
     * @return MsModel of the microservice communication
     */
    public static MsModel getMsModel(String path) throws IOException {
        // get a parser instance
        SourceParser parser = new SourceParser();

        // get the full paths to the microservice directories
        // List<String> msPaths = Arrays.asList(DirectoryUtils.getMsPaths(path)).stream().map(ms -> path + "/" + ms).collect(Collectors.toList());
        List<String> msPaths = Arrays.asList(DirectoryUtils.getMsFullPaths(path));

        // get the microservice communication model
        return parser.createMsModel(msPaths);
    }

    public static MsModel getMsModel(List<String> msFullPaths) throws IOException {
        SourceParser parser = new SourceParser();
        return parser.createMsModel(msFullPaths);
    }

}
