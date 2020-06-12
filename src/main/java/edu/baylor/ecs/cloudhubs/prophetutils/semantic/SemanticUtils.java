package edu.baylor.ecs.cloudhubs.prophetutils.semantic;

import com.github.javaparser.ast.expr.Name;
import edu.baylor.ecs.cloudhubs.jparser.component.Component;
import edu.baylor.ecs.cloudhubs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.AnnotationComponent;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.ClassComponent;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.MethodInfoComponent;
import edu.baylor.ecs.cloudhubs.jparser.model.AnnotationValuePair;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.EntityContextAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.jparser.JParserUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Jan Svacina
 * Creates semantic code clones from microservice mesh
 */
public class SemanticUtils {

    /**
     * Get semantic code clones in form of html as list of strings
     * @param msPath root path to microservice
     * @param msPaths microservices in the root path
     * @return html as list of strings
     */
    public List<String> getClonesInLines(String msPath, String[] msPaths) {
        AnalysisContext analysisContext = createAnalysisContext(msPath);
        HashMap<String, List<EndpointContext>> endpointContext = createEndpointContexts(analysisContext, msPaths);
        List<ClonePairContext> clonePairContext = createClonePairContexts(endpointContext);
        List<String> htmlLines = generateHtmlLines(clonePairContext);
        printHtmlToFile(htmlLines);
        return htmlLines;
    }

    /**
     * Creates initial analysis context
     * @param msPath root directory with microservices
     * @return analysis context
     */
    private AnalysisContext createAnalysisContext(String msPath) {
        JParserUtils jParserUtils = JParserUtils.getInstance();
        return jParserUtils.createAnalysisContextFromDirectory(msPath);
    }

    /**
     * Creates list of endpoint contexts
     * @param ac preliminary context
     * @return list of endpoint contexts
     */
    private HashMap<String, List<EndpointContext>> createEndpointContexts(AnalysisContext ac, String[] msPaths) {
        HashMap<String, Set<ClassComponent>> clusters = EntityContextAdapter.clusterClassComponents(ac.getModules(), msPaths);
        List<Map.Entry<String, List<ClassComponent>>> restControllers = getRestControllers(clusters);
        HashMap<String, List<EndpointContext>> endpointContexts = new HashMap<>();
        for (Map.Entry<String, List<ClassComponent>> restController: restControllers) {
            for (ClassComponent component : restController.getValue()) {
                for (int k = 0; k < component.getMethods().size(); k++) {
                    MethodInfoComponent method = component.getMethods().get(k).asMethodInfoComponent();
                    EndpointContext endpointContext = new EndpointContext();
                    endpointContext.setMs(restController.getKey());
                    endpointContext.setMsClass(component.getContainerName());
                    endpointContext.setMethod(method.getMethodName());
                    endpointContext.setReturnType(method.getReturnType());
                    for (int i = 0; i < method.getAnnotations().size(); i++){
                        AnnotationComponent annotation = (AnnotationComponent) method.getAnnotations().get(i);
                        for (int j = 0; j < annotation.getAnnotationValuePairList().size(); j++) {
                            AnnotationValuePair annotationValuePair = annotation.getAnnotationValuePairList().get(j);
                            System.out.println(annotationValuePair);
                            if (annotationValuePair.getValue().contains("RequestMethod")) {
                                String[] split = annotationValuePair.getValue().split("\\.");
                                endpointContext.setHttpMethod(split[1]);
                            }
                        }
                    }
                    List<EndpointContext> values = endpointContexts.get(restController.getKey());
                    if (values == null) {
                        values = new ArrayList<>();
                    }
                    values.add(endpointContext);
                    endpointContexts.put(restController.getKey(),values);
                }
            }
        }
        return endpointContexts;
    }

    /**
     * Get controllers classes from whole pool
     * @param clusters
     * @return
     */
    private List<Map.Entry<String, List<ClassComponent>>> getRestControllers(HashMap<String, Set<ClassComponent>> clusters) {
        HashMap<String, List<ClassComponent>> restControllers = new HashMap<>();
        for (Map.Entry<String, Set<ClassComponent>> entry : clusters.entrySet()) {
            restControllers.put(entry.getKey(), new ArrayList<>());
            for (ClassComponent classComponent : entry.getValue()) {
                List<Component> annotations = classComponent.getAnnotations();
                for (int j = 0; j < annotations.size(); j++) {
                    AnnotationComponent annotationComponent = (AnnotationComponent) annotations.get(j);
                    Name annotationName = annotationComponent.getAnnotation().getName();
                    if (annotationName.getIdentifier().equals("RestController")) {
                        List<ClassComponent> values = restControllers.get(entry.getKey());
                        values.add(classComponent);
                        restControllers.put(entry.getKey(), values);
                        break;
                    }
                }
            }
        }
        return new ArrayList<>(restControllers.entrySet());
    }

    /**
     * Compares all endpoints to get clones
     * @param endpointContextMap microservice and its endpoints
     * @return
     */
    private List<ClonePairContext> createClonePairContexts(HashMap<String, List<EndpointContext>> endpointContextMap) {
        List<ClonePairContext> clonePairContexts = new ArrayList<>();
        List<String> keys = new ArrayList<>(endpointContextMap.keySet());
        for (int i = 0; i < keys.size() - 1; i++) {
            String keyI = keys.get(i);
            List<EndpointContext> endpointContextsI = endpointContextMap.get(keyI);
            for (int j = i + 1; j < keys.size(); j++) {
                String keyJ = keys.get(j);
                List<EndpointContext> endpointContextsJ = endpointContextMap.get(keyJ);
                // compare all endpoint contexts together
                for (int k = 0; k < endpointContextsI.size() -1; k++) {
                    EndpointContext ecK = endpointContextsI.get(k);
                    for (int l = 0; l < endpointContextsJ.size(); l++) {
                        EndpointContext ecL = endpointContextsJ.get(l);

//                        if (ecK.getMethod().equals(ecL.getMethod())) {
//                            System.out.println("");
//                        }

                        ClonePairContext clonePairContext = isClonePair(ecK, ecL);
                        if (clonePairContext != null) {
                            clonePairContexts.add(clonePairContext);
                        }
                    }
                }
            }
        }
        return clonePairContexts;
    }

    private ClonePairContext isClonePair(EndpointContext endpointContextA, EndpointContext endpointContextB) {
        double sameness = 0.0;
        // http Method
        sameness += compareStrings(endpointContextA.getHttpMethod(), endpointContextB.getHttpMethod());
        // return type
        sameness += compareStrings(endpointContextA.getReturnType(), endpointContextB.getReturnType());
        // method
        sameness += compareStrings(endpointContextA.getMethod(), endpointContextB.getMethod());
        // ms
        sameness += compareStrings(endpointContextA.getMsClass(), endpointContextB.getMsClass());
        // arguments
        sameness += compareArguments(endpointContextA.getArguments(), endpointContextB.getArguments());
        System.out.println(sameness);
        return (sameness/5.0) > 0.4 ? new ClonePairContext(endpointContextA, endpointContextB, sameness / 5.0) : null;
    }

    private double compareStrings(String a, String b) {
        return a.equals(b) ? 1.0 : 0.0;
    }

    private double compareArguments(List<String> argsA, List<String> argsB) {
        if (argsA == null || argsB == null) {
            return 0.0;
        }
        List<String> min = argsA.size() >= argsB.size() ? argsB : argsA;
        List<String> max = argsA.size() < argsB.size() ? argsA : argsB;
        int denominator = max.size();
        int nominator = 0;
        for (String s: min) {
            for (String x: max) {
                if (s.equals(x)) {
                    nominator++;
                }
            }
        }
        return denominator != 0 ? nominator / denominator : 0;
    }

    private void printHtmlToFile(List<String> htmlLines) {
        List<String> html = new ArrayList<>();
        html.addAll(addLines("src/main/resources/header.html"));
        html.addAll(htmlLines);
        html.addAll(addLines("src/main/resources/footer.html"));
        write(html);
    }

    private void write(List<String> htmlLines){
        try {
            FileWriter myWriter = new FileWriter("semantics.html");
            for (String htmlLine : htmlLines) {
                myWriter.write(htmlLine + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> addLines(String path) {
        List<String> html = new ArrayList<>();
        try {
            File header = new File(path);
            Scanner headerReader = new Scanner(header);
            while (headerReader.hasNextLine()) {
                String data = headerReader.nextLine();
                html.add(data);
            }
            headerReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return html;
    }

    private List<String> generateHtmlLines(List<ClonePairContext> clonePairContext) {
        List<String> htmlLines = new ArrayList<>();
        htmlLines.add("classDiagram");
        for (ClonePairContext clone: clonePairContext) {
            htmlLines.addAll(generateHtmlForEndpoint(clone.getFirst()));
            htmlLines.addAll(generateHtmlForEndpoint(clone.getSecond()));
            htmlLines.addAll(generateEdge(clone));
        }
        return htmlLines;
    }

    private Collection<? extends String> generateEdge(ClonePairContext clone) {
        List<String> html = new ArrayList<>();
        html.add(getName(clone.getFirst())  + " --> " + getName(clone.getSecond()));
        return html;
    }

    private Collection<? extends String> generateHtmlForEndpoint(EndpointContext first) {
        List<String> html = new ArrayList<>();
        String nameClass = getName(first);
        String returnType = first.getReturnType().replace("<", "");
        returnType = returnType.replace(">", "");
        html.add("class " + nameClass);
        html.add(nameClass + " : Ms " + getClassName(first.getMs()));
        html.add(nameClass + " : MsClass " + first.getMsClass());
        html.add(nameClass + " : Method " + first.getMethod());
        html.add(nameClass + " : HttpMethod " + first.getHttpMethod());
        html.add(nameClass + " : ReturnType " + returnType);
        return html;
    }

    private String getClassName(String className) {
        String[] split = className.split("\\/");
        return split[split.length - 1];
    }

    private String getName(EndpointContext endpointContext) {
        return getClassName(endpointContext.getMs()) + endpointContext.getMethod(); //+ endpointContext.getMsClass()
    }


    public void analyze(){
        String[] msPaths = new String[2];
        msPaths[0] = ("/Users/svacina/git/tms2/cms");
        msPaths[1] = ("/Users/svacina/git/tms2/ems");
        JParserUtils jParserUtils = JParserUtils.getInstance();
        AnalysisContext ac = jParserUtils.createAnalysisContextFromDirectory("/Users/svacina/git/tms2");
//        AnalysisContext ac = ProphetUtilsFacade.getAnalysisContext("/Users/svacina/git/tms2");
        HashMap<String, List<ClassComponent>> restControllers = new HashMap<>();
        HashMap<String, Set<ClassComponent>> clusters = EntityContextAdapter.clusterClassComponents(ac.getModules(), msPaths);

        // select only RestControllers
        for (Map.Entry<String, Set<ClassComponent>> entry : clusters.entrySet()) {
            restControllers.put(entry.getKey(), new ArrayList<>());
            for (ClassComponent classComponent : entry.getValue()) {
                List<Component> annotations = classComponent.getAnnotations();
                for (int j = 0; j < annotations.size(); j++) {
                    AnnotationComponent annotationComponent = (AnnotationComponent) annotations.get(j);
                    Name annotationName = annotationComponent.getAnnotation().getName();
                    if (annotationName.getIdentifier().equals("RestController")) {
                        List<ClassComponent> values = restControllers.get(entry.getKey());
                        values.add(classComponent);
                        restControllers.put(entry.getKey(), values);
                        break;
                    }
                }

            }
        }

        //formatting
        List<Map.Entry<String, Set<ClassComponent>>> entryList = new ArrayList<>();
        for (Map.Entry<String, Set<ClassComponent>> entry : clusters.entrySet()) {
            entryList.add(entry);
        }

        //compare each cluster with each cluster
        for (int i = 0; i < entryList.size(); i++) {
            for (int j = i + 1; j< entryList.size(); j++) {
                Set<ClassComponent> setA = entryList.get(i).getValue();
                Set<ClassComponent> setB = entryList.get(j).getValue();
                Iterator<ClassComponent> itA = setA.iterator();
                Iterator<ClassComponent> itB = setB.iterator();
                while (itA.hasNext()) {
                    ClassComponent componentA = itA.next();
                    while (itB.hasNext()) {
                        ClassComponent componentB = itB.next();
                        for (int k = 0; k < componentA.getMethods().size(); k++) {
                            MethodInfoComponent methodA = (MethodInfoComponent) componentA.getMethods().get(k);
                            for (int l = 0; l < componentB.getMethods().size(); l++) {
                                MethodInfoComponent methodB = (MethodInfoComponent) componentB.getMethods().get(l);
                                // are methods match?
                                // 1. annotations
                                List<Component> annotationsA = methodA.getAnnotations();
                                List<Component> annotationsB = methodB.getAnnotations();

                            }
                        }
                    }
                }
            }
        }

    }
}
