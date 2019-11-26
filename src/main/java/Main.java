import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import systemcontext.SystemContextParser;

public class Main {
    public static void main(String[] args) {
        //Nobody sees this
        String rootPath = "/Users/svacina/git/c2advseproject";
        String[] microServicePaths = new String[5];
        microServicePaths[0] = "/Users/svacina/git/c2advseproject/cms";
        microServicePaths[1] = "/Users/svacina/git/c2advseproject/qms-backend";
        microServicePaths[2] = "/Users/svacina/git/c2advseproject/ems";
        microServicePaths[3] = "/Users/svacina/git/c2advseproject/user-management";
        microServicePaths[4] = "/Users/svacina/git/c2advseproject/user-authentication";
        SystemContext systemContext = ProphetUtilsFacade.getSystemContext(rootPath, microServicePaths);
        System.out.println(systemContext.getSystemName());
    }
}
