import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.ProphetUtilsFacade;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import edu.baylor.ecs.prophet.bounded.context.api.impl.BoundedContextApiImpl;

import java.util.Arrays;

public class LegacyMain {


    public static void main(String[] args) {
        SystemContext ctx = ProphetUtilsFacade.getEntityContext(Arrays.asList(
                "/Users/dkozak-mac-uni/projects/prophet/tms/cms",
                "/Users/dkozak-mac-uni/projects/prophet/tms/ems",
                "/Users/dkozak-mac-uni/projects/prophet/tms/qms"
        ));
        FileManager.writeToFile(ctx, "legacy-prophet.json");
        BoundedContext boundedContext = new BoundedContextApiImpl().getBoundedContext(ctx, false);
        FileManager.writeToFile(boundedContext, "legacy-bounded-context.json");
    }
}
