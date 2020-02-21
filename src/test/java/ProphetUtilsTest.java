import edu.baylor.ecs.cloudhubs.prophetdto.communication.ContextMap;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.ProphetUtilsFacade;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JunitConfig.class)
@TestPropertySource(locations = "/application.properties")
public class ProphetUtilsTest {

    @Value("${user.rootPath}")
    private String rootPath;

    @Value("${user.cmsPath}")
    private String cms_path;

    @Value("${user.qmsPath}")
    private String qms_path;

    @Value("${user.emsPath}")
    private String ems_path;

    @Value("${user.umsPath}")
    private String ums_path;

    private String[] microServicePaths;

    @BeforeEach
    public void initSystems(){
        //rootPath.setRootPath("/Users/svacina/git/c2advseproject/");
        microServicePaths = new String[4];
        microServicePaths[0] = rootPath + cms_path;
        microServicePaths[1] = rootPath + qms_path;
        microServicePaths[2] = rootPath + ems_path;
        microServicePaths[3] = rootPath + ums_path;
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
        String[] paths = new String[] {ems_path, cms_path};
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
        ContextMap mg =  ProphetUtilsFacade.getContextMap("/Users/svacina/tmp/tms");
        assertNotNull(mg);
    }

}
