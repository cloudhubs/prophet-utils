package edu.baylor.ecs.cloudhubs.prophetutils.bounded;

import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Entity;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Module;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class SimpleBoundedUtils {

    public static BoundedContext getBoundedContext(SystemContext systemContext){
        Set<Module> modules = systemContext.getModules();
        Stack<Module> moduleStack = new Stack<>();
        for (Module m: modules
        ) {
            moduleStack.add(m);
        }
//        moduleStack.addAll(modules);
        while(moduleStack.size() > 1){
            Module m1 = moduleStack.pop();
            Module m2 = moduleStack.pop();
            Module result = mergeModules(m1, m2);
            if (result.getEntities().size() > 0){
                moduleStack.push(result);
            }
        }

        if (moduleStack.size() > 0){
            Module m = moduleStack.get(0); //ToDo: unsafe
            //create the bounded context
            // use the name of the system context as the name of the bounded context
            BoundedContext toReturn = new BoundedContext(systemContext.getSystemName(), m.getEntities());
            return toReturn;
        } else {
            BoundedContext toReturn = new BoundedContext(systemContext.getSystemName(), null);
            return toReturn;
        }

    }

    private static Module mergeModules(Module m1, Module m2) {
        Module toReturn = new Module();
        toReturn.setEntities(m1.getEntities());
        toReturn.setName(m1.getName());

        for (Entity e2: m2.getEntities()
        ) {
            boolean merged = false;
            for (Entity re: toReturn.getEntities()
                 ) {
                if (re.getEntityName().equals(e2.getEntityName())){
                    //merge two entities
                    Set<Field> fields = getFields(re.getFields(), e2.getFields());
                    re.setFields(fields);
                    merged = true;
                    break;
                }
            }
            if (!merged){
                Set<Entity> entities = toReturn.getEntities();
                entities.add(e2);
                toReturn.setEntities(entities);
            }
        }

        return toReturn;
    }

    private static Set<Field> getFields(Set<Field> e1Fields, Set<Field> e2Fields){
        Set<Field> toReturn = new HashSet<>();
        for (Field f: e1Fields
             ) {
            if (!f.getName().equals("id")){
                toReturn.add(f);
            }
        }
        for (Field f: e2Fields
        ) {
            List<Field> same =
                    e1Fields.stream().filter(n -> (n.getName().equals(f.getName()) && n.getType().equals(f.getType())))
                            .collect(Collectors.toList());
            if (!f.getName().equals("id") && same.size() == 0){
                toReturn.add(f);
            }
        }
        return toReturn;
    }
}
