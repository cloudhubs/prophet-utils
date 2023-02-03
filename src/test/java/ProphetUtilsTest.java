import com.github.javaparser.ast.expr.Name;
import edu.baylor.ecs.cloudhubs.jparser.component.Component;
import edu.baylor.ecs.cloudhubs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.AnnotationComponent;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.ClassComponent;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.MethodInfoComponent;
import edu.baylor.ecs.cloudhubs.prophetdto.app.ProphetAppData;
import edu.baylor.ecs.cloudhubs.prophetdto.app.utilsapp.GitReq;
import edu.baylor.ecs.cloudhubs.prophetdto.app.utilsapp.RepoReq;
import edu.baylor.ecs.cloudhubs.prophetdto.communication.ContextMap;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.ProphetUtilsFacade;
import edu.baylor.ecs.cloudhubs.prophetutils.adapter.EntityContextAdapter;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import edu.baylor.ecs.cloudhubs.prophetutils.semantic.SemanticUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JunitConfig.class)
@Slf4j
public class ProphetUtilsTest {

    @Value("./msJar/tms/")
    private String rootPath;

    @Value("cms/cms.jar")
    private String cms_path;

    @Value("qms/qms.jar")
    private String qms_path;

    @Value("ems/ems.jar")
    private String ems_path;

    // TODO: Where do I find UMS?
//    @Value("${user.umsPath}")
//    private String ums_path;

    private String[] microServicePaths;

    @BeforeEach
    public void initSystems(){
        microServicePaths = new String[3];
        microServicePaths[0] = rootPath + cms_path;
        microServicePaths[1] = rootPath + qms_path;
        microServicePaths[2] = rootPath + ems_path;
//        microServicePaths[3] = rootPath + ums_path;
//        Arrays.asList(microServicePaths).forEach(log::info);
    }

    @Test
    @DisplayName("entity context generation")
    public void entityContextGen() {
        SystemContext entityContext = ProphetUtilsFacade.getEntityContext(rootPath, microServicePaths);
        assertNotNull(entityContext.getSystemName());
    }

    @Test
    @DisplayName("bounded entity context generation")
    public void boundedContextGen() {
        BoundedContext boundedContext = ProphetUtilsFacade.getBoundedContext(rootPath, microServicePaths);
        assertNotNull(boundedContext.getSystemName());
    }

    @Test
    @DisplayName("generating graph")
    public void generatingGraph(){
        String template = FileManager.readHtmlTemplates("src/main/resources/header.html");
        template += FileManager.readHtmlTemplates("src/main/resources/footer.html");
        assertNotNull(template);
    }

    @Test
    @DisplayName("generating list")
    public void generateList(){
        List<String> list = ProphetUtilsFacade.createHtmlTemplate(rootPath, microServicePaths);
        assertNotNull(list);
    }

    @Test
    @DisplayName("generating template")
    public void generateTemplate(){
        List<String> list = ProphetUtilsFacade.createHtmlTemplate(rootPath, microServicePaths);
        FileManager.writeBoundedContextToFile(list);
        assertNotNull(list);
    }

    @Test
    @DisplayName("generating TMS2 template")
    public void generateTms2Template(){
        String[] paths = new String[] { ems_path, cms_path};
        List<String> list = ProphetUtilsFacade.createHtmlTemplate(rootPath, paths);
        FileManager.writeBoundedContextToFile(list);
        assertNotNull(list);
    }

    @Test
    @DisplayName("get mermaid graph")
    public void getMermaidGraph(){
        MermaidGraph mg =  ProphetUtilsFacade.getMermaidGraph(rootPath, microServicePaths);
        assertNotNull(mg);
    }

    @Test
    @DisplayName("get context map")
    public void getContextMap(){
        ContextMap mg =  ProphetUtilsFacade.getContextMap(rootPath);
        assertNotNull(mg);
    }

    // TODO: setup later on after CI/CD finished
//    @Test
//    @DisplayName("prophet data")
//    public void testProphetData() throws IOException {
//        ProphetAppData data = new ProphetAppData();
//        GitReq request = new GitReq();
//        List<RepoReq> repoReqs = new ArrayList<>();
//        RepoReq rr = new RepoReq();
//        rr.setPath("/Users/svacina/git/cdh/prophet-app-utils/repos-1589488397/train-ticket");
//        rr.setMonolith(false);
//        repoReqs.add(rr);
//        request.setRepositories(repoReqs);
//        data = ProphetUtilsFacade.getProphetAppData(request);
//        assertNotNull(data);
//    }


    @Test
    @DisplayName("")
    public void testAnalysisContext1() {

        SemanticUtils semanticUtils = new SemanticUtils();
        String[] msPaths = {rootPath + cms_path, rootPath + ems_path};

        List<String> lines = semanticUtils.getClonesInLines(rootPath, msPaths);
        for (String s: lines) {
            System.out.println(s);
        }
    }


//    @Test
//    @DisplayName("")
//    public void testAnalysisContext() {
//        String[] msPaths = new String[2];
//        msPaths[0] = ("/Users/svacina/git/tms2/cms");
//        msPaths[1] = ("/Users/svacina/git/tms2/ems");
//        AnalysisContext ac = ProphetUtilsFacade.getAnalysisContext("/Users/svacina/git/tms2");
//        Map<String, List<ClassComponent>> restControllers = new HashMap<>();
//        HashMap<String, Set<ClassComponent>> clusters = EntityContextAdapter.clusterClassComponents(ac.getModules(), msPaths);
//        for (Map.Entry<String, Set<ClassComponent>> entry : clusters.entrySet()) {
//            restControllers.put(entry.getKey(), new ArrayList<>());
//            for (ClassComponent classComponent : entry.getValue()) {
//                List<Component> annotations = classComponent.getAnnotations();
//                for (int j = 0; j < annotations.size(); j++) {
//                    AnnotationComponent annotationComponent = (AnnotationComponent) annotations.get(j);
//                    Name annotationName = annotationComponent.getAnnotation().getName();
//                    if (annotationName.getIdentifier().equals("RestController")) {
//                        List<ClassComponent> values = restControllers.get(entry.getKey());
//                        values.add(classComponent);
//                        restControllers.put(entry.getKey(), values);
//                        break;
//                    }
//                }
//
//            }
//        }
//        List<Map.Entry<String, Set<ClassComponent>>> entryList = new ArrayList<>();
//        for (Map.Entry<String, Set<ClassComponent>> entry : clusters.entrySet()) {
//            entryList.add(entry);
//        }
//
//        for (int i = 0; i < entryList.size(); i++) {
//            for (int j = i + 1; j< entryList.size(); j++) {
//                Set<ClassComponent> setA = entryList.get(i).getValue();
//                Set<ClassComponent> setB = entryList.get(j).getValue();
//                Iterator<ClassComponent> itA = setA.iterator();
//                Iterator<ClassComponent> itB = setB.iterator();
//                while (itA.hasNext()) {
//                    ClassComponent componentA = itA.next();
//                    while (itB.hasNext()) {
//                        ClassComponent componentB = itB.next();
//                        for (int k = 0; k < componentA.getMethods().size(); k++) {
//                            MethodInfoComponent methodA = (MethodInfoComponent) componentA.getMethods().get(k);
//                            for (int l = 0; l < componentB.getMethods().size(); l++) {
//                                MethodInfoComponent methodB = (MethodInfoComponent) componentB.getMethods().get(l);
//                                // are methods match?
//
//                            }
//
//                        }
//
//
//
//                    }
//                }
//
//            }
//        }
//
//    }

}
