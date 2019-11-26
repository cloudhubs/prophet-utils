import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import systemcontext.SystemContextParser;

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
    public static BoundedContext getBoundedContext(String path) {
        return null;
    }


    public static SystemContext getSystemContext(String path, String[] msPaths){
        SystemContextParser systemContextParser = SystemContextParser.getInstance();
        return systemContextParser.createSystemContextFromPathViaAnalysisContext(path, msPaths);
    }


}
