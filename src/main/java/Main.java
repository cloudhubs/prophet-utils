import com.google.gson.Gson;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetutils.ProphetUtilsFacade;
import edu.baylor.ecs.cloudhubs.prophetutils.filemanager.FileManager;
import edu.baylor.ecs.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import edu.baylor.ecs.prophet.bounded.context.api.impl.BoundedContextApiImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);
        SystemContext ctx = ProphetUtilsFacade.getSystemContextViaNativeImage(analysisRequest.getMicroservices());
        FileManager.writeToFile(ctx, "ni-prophet.json");
        System.out.println(gson.toJson(ctx));
        BoundedContext boundedContext = new BoundedContextApiImpl().getBoundedContext(ctx, false);
        FileManager.writeToFile(boundedContext, "ni-bounded-context.json");
    }
}
