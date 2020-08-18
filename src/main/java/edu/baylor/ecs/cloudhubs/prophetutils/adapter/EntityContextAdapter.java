package edu.baylor.ecs.cloudhubs.prophetutils.adapter;

import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.*;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Module;
import edu.baylor.ecs.cloudhubs.jparser.component.Component;
import edu.baylor.ecs.cloudhubs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.AnnotationComponent;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.ClassComponent;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.FieldComponent;
import edu.baylor.ecs.cloudhubs.jparser.component.impl.ModuleComponent;

import java.util.*;

/**
 * JParser AnalysisContext -> ProphetDTO SystemContext
 */
public class EntityContextAdapter {

    /**
     * Retrieves entity model context from a system based on JParser representation
     * @param context JParser AnalysisContext
     * @return SystemContext
     */
    public static SystemContext getSystemContext(AnalysisContext context, String[] msPaths) {
        Set<Module> modules = new HashSet<>();
        HashMap<String, Set<ClassComponent>> clusters = clusterClassComponents(context.getModules(), msPaths);

        for (Map.Entry<String, Set<ClassComponent>> entry : clusters.entrySet()) {
            Module module_n = new Module();
            Set<Entity> entities = new HashSet<>();
            for (ClassComponent clazz : entry.getValue()) {
                List<Component> classAnnotations = clazz.getAnnotations();
                if (classAnnotations != null){
                    for (Component cmp: classAnnotations) {
                        AnnotationComponent ac = (AnnotationComponent) cmp;
                        if (ac.getAsString().equals("@Entity") || ac.getAsString().equals("@Document") || ac.getAsString().equals("@Data")){
                            Set<Field> fields = new HashSet<>();
                            for (FieldComponent field : clazz.getFieldComponents()) {
                                Field field_n = new Field();
                                field_n.setName(new Name(field.getFieldName()));
                                if (isCollection(field.getType())){
                                    String s = field.getType();
                                    String entityRef = s.substring(s.indexOf("<") + 1, s.indexOf(">"));
                                    field_n.setType(entityRef);
                                    field_n.setCollection(true);
                                } else {
                                    field_n.setType(field.getType());
                                    field_n.setCollection(false);
                                }
                                Set<Annotation> annotations = new HashSet<>();
                                for (Component annotation : field.getAnnotations()) {
                                    Annotation ann = new Annotation();
                                    ann.setStringValue(annotation.asAnnotationComponent().getAnnotationValue());
                                    ann.setName(annotation.asAnnotationComponent().getAsString());
                                    annotations.add(ann);
                                }
                                field_n.setAnnotations(annotations);
                                for (Annotation a: field_n.getAnnotations()){
                                    if (a.getName().equals("@ManyToOne") || a.getName().equals("@OneToMany" )
                                            || a.getName().equals("@OneToOne") || a.getName().equals("@ManyToMany")) {
                                        //field_n.setEntityReference();
                                        field_n.setReference(true);
                                        field_n.setEntityRefName(field_n.getType());
                                    }
                                }
                                fields.add(field_n);
                            }
                            Entity entity = new Entity(clazz.getClassName());
                            entity.setFields(fields);
                            entities.add(entity);
                        }
                    }
                }
            }

            module_n.setName(new Name(entry.getKey()));
            module_n.setEntities(entities);
            modules.add(module_n);
        }

        return new SystemContext(context.getRootPath(), modules);
    }



    /**
     * Cluster classes by their presence in respective ms modules
     * @param moduleComponents
     * @param msPaths
     * @return
     */
    public static HashMap<String, Set<ClassComponent>> clusterClassComponents(List<ModuleComponent> moduleComponents,
                                                                       String[] msPaths){
        HashMap<String, Set<ClassComponent>> clusters = new HashMap<>();
        for (String path: msPaths){
            clusters.put(path, new HashSet<ClassComponent>());
        }
        for (ModuleComponent mc: moduleComponents) {
            String mcPath = mc.getPath();
            String msPath = Arrays.stream(msPaths).filter(mcPath::contains).findFirst().orElse(null);
            if (msPath != null){
                Set<ClassComponent> valueSet = clusters.get(msPath);
                valueSet.addAll(mc.getClasses());
                clusters.put(msPath, valueSet);
            }
        }
        return clusters;
    }


    public static boolean isCollection(String type){
        if (type.contains("Set") ){
            return true;
        } else if (type.contains("Collection")){
            return true;
        } else return type.contains("List");
    }
}
