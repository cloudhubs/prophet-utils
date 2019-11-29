package systemcontext;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.prophet.bounded.context.utils.BoundedContextUtils;
import edu.baylor.ecs.prophet.bounded.context.utils.impl.BoundedContextUtilsImpl;
import filemanager.FileManager;
import systemcontext.SystemContextParser;

import java.io.*;

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
     * Get Data via Jparser, Create SystemContext, pass SystemContext to bounded-context lib
     * @param path
     * @return BoundedContext
     */
    public static BoundedContext getBoundedContext(String path, String[] msPaths) {
        BoundedContextUtils boundedContextUtils = new BoundedContextUtilsImpl();
        SystemContext systemContext = ProphetUtilsFacade.getSystemContext(path, msPaths);
        FileManager.writeToFile(systemContext);
        BoundedContext boundedContext = boundedContextUtils.createBoundedContext(systemContext);

        return boundedContext;
    }


    public static SystemContext getSystemContext(String path, String[] msPaths){
        SystemContextParser systemContextParser = SystemContextParser.getInstance();
        return systemContextParser.createSystemContextFromPathViaAnalysisContext(path, msPaths);
    }


}
