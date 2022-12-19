import com.google.gson.Gson;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.ProphetUtilsFacade;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import edu.baylor.ecs.cloudhubs.prophetutils.nativeimage.MicroserviceInfo;
import edu.baylor.ecs.prophet.bounded.context.api.impl.BoundedContextApiImpl;

import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        String baseDir = "/Users/dkozak/Projects/prophet/jars";
        List<MicroserviceInfo> microservices = Arrays.asList(
                new MicroserviceInfo(baseDir, "edu.baylor.ecs.cms", "cms"),
                new MicroserviceInfo(baseDir, "edu.baylor.ems", "ems"),
                new MicroserviceInfo(baseDir, "baylor.csi", "qms"));

        SystemContext ctx = ProphetUtilsFacade.getSystemContextViaNativeImage(microservices);
        Gson gson = new Gson();
        FileManager.writeToFile(ctx, "ni-prophet.json");
        System.out.println(gson.toJson(ctx));
        BoundedContext boundedContext = new BoundedContextApiImpl().getBoundedContext(ctx, false);
        FileManager.writeToFile(boundedContext, "ni-bounded-context.json");
    }
}
