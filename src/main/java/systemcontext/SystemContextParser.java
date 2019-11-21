package systemcontext;

import edu.baylor.ecs.ciljssa.component.Component;
import edu.baylor.ecs.ciljssa.component.context.AnalysisContext;
import edu.baylor.ecs.ciljssa.component.impl.ClassComponent;
import edu.baylor.ecs.ciljssa.component.impl.DirectoryComponent;
import edu.baylor.ecs.ciljssa.component.impl.FieldComponent;
import edu.baylor.ecs.ciljssa.component.impl.ModuleComponent;
import edu.baylor.ecs.ciljssa.factory.context.AnalysisContextFactory;
import edu.baylor.ecs.ciljssa.factory.directory.DirectoryFactory;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Annotation;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Entity;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Field;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Module;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Non-static methods because it requires the initialization of the appropriate factories for JParser.
 *
 * Similarly, when running these factories perhaps multiple times on a single session, you should use the same
 * factories to avoid duplicate IDs being generated. This is fixed by just keeping the factories loaded.
 *
 * Get instance of this singleton via getInstance() and call desired methods from there.
 *
 * @author Jonathan Simmons
 *
 * TODO: - Add functionality for mapping a single module to a SystemContext
 *       - Make an efficient method for populating a SystemContext for a single file
 */
public class SystemContextParser {

    /**
     * Version of JParser currently being used
     */
    public static final int JPARSER_VERSION = 4;

    /**
     * Version of the Prophet-DTO being used
     */
    public static final int PROPHET_DTO_VERSION = 1;

    private static SystemContextParser INSTANCE;

    private static DirectoryFactory directoryFactory;
    private static AnalysisContextFactory contextFactory;

    /**
     * Singleton instance getter
     * @return gets this instance of systemcontextparser
     */
    public static SystemContextParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemContextParser();
        }
        return INSTANCE;
    }

    /**
     * Private constructor for singleton
     */
    private SystemContextParser() {
        directoryFactory = new DirectoryFactory();
        contextFactory = new AnalysisContextFactory();
    }

    /**
     * Create an analysis context object directly via some string path.
     * @param path Path to directory - not to file.
     * @return an AnalysisContext object
     */
    public AnalysisContext createAnalysisContextFromDirectory(String path) {
        DirectoryComponent directory = (DirectoryComponent) directoryFactory.createDirectoryGraph(path);
        return contextFactory.createAnalysisContextFromDirectoryGraph(directory);
    }

    /**
     * Create an analysis context object directly for a given single file. Still populates whole context object which
     * has large overhead. If all you need is information regarding a single class or single module without any metadata,
     * this is not the best way.
     * @param file File to parse
     * @return an AnalysisContext object for the file
     */
    public AnalysisContext createAnalysisContextFromFile(File file) {
        DirectoryComponent directory = (DirectoryComponent) directoryFactory.createDirectoryGraphOfFile(file);
        return contextFactory.createAnalysisContextFromDirectoryGraph(directory);
    }

    /**
     * Create an analysis context object directly for a given single file. Still populates whole context object which
     * has large overhead. If all you need is information regarding a single class or single module without any metadata,
     * this is not the best way.
     * @param filePath Path of the file to parse
     * @return an AnalysisContext object for the file
     */
    public AnalysisContext createAnalysisContextFromFile(String filePath) {
        DirectoryComponent directory = (DirectoryComponent) directoryFactory
                .createDirectoryGraphOfFile(new File(filePath));
        return contextFactory.createAnalysisContextFromDirectoryGraph(directory);
    }

    /**
     * Create a SystemContext object for Prophet via an AnalysisContext object created from the path.
     * @param path Path to directory to create AnalysisContext for populating SystemContext.
     * @return A SystemContext object
     */
    public SystemContext createSystemContextFromPathViaAnalysisContext(String path) {
        return createSystemContextFromAnalysisContext(createAnalysisContextFromDirectory(path));
    }

    /**
     * Create a SystemContext object for Prophet via an AnalysisContext object.
     * @param context an AnalysisContext for populating SystemContext.
     * @return A SystemContext object
     */
    public SystemContext createSystemContextFromAnalysisContext(AnalysisContext context) {
        Set<Module> modules = new HashSet<>();
        for (ModuleComponent module : context.getModules()) {
            Module module_n = new Module();
            Set<Entity> entities = new HashSet<>();
            for (ClassComponent clazz : module.getClasses()) {
                Set<Field> fields = new HashSet<>();
                for (FieldComponent field : clazz.getFieldComponents()) {
                    Field field_n = new Field();
                    field_n.setName(field.getFieldName());
                    field_n.setType(field.getType());
                    Set<Annotation> annotations = new HashSet<>();

                    for (Component annotation : field.getAnnotations()) {
                        Annotation ann = new Annotation();
                        ann.setStringValue(annotation.asAnnotationComponent().getAnnotationValue());
                        ann.setName(annotation.asAnnotationComponent().getAsString());
                        annotations.add(ann);
                    }
                    field_n.setAnnotations(annotations);
                    fields.add(field_n);
                }
                Entity entity = new Entity(clazz.getClassName());
                entity.setFields(fields);
                entities.add(entity);
            }
            module_n.setName(module.getInstanceName());
            module_n.setEntities(entities);
            modules.add(module_n);
        }
        return new SystemContext(context.getRootPath(), modules);
    }

}
