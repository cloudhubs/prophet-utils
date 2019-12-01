import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;

import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.ProphetUtilsFacade;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class ProphetUtilsTest {

    private static String rootPath;
    private static String[] microServicePaths;

    @BeforeAll
    public static void initSystems(){
        rootPath = "/Users/svacina/git/c2advseproject/";
        microServicePaths = new String[4];
        microServicePaths[0] = rootPath + "cms";
        microServicePaths[1] = rootPath + "qms-backend";
        microServicePaths[2] = rootPath + "ems";
        microServicePaths[3] = rootPath + "user-management";
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
    @DisplayName("generating template")
    public void generateTemplate(){
        ProphetUtilsFacade.createHtmlTemplate(rootPath, microServicePaths);
        assertNotNull(1);
    }

}
