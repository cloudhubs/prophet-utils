package edu.baylor.ecs.cloudhubs.prophetutils.adapter;

import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidEdge;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidNode;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Entity;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bounded Context -> Nodes and edges
 */
public class EntityGraphAdapter {

    public static MermaidGraph getMermaidGraph(BoundedContext boundedContext){
        List<MermaidNode> mermaidNodes = new ArrayList<>();
        List<MermaidEdge> mermaidEdges = new ArrayList<>();
        for (Entity entity: boundedContext.getBoundedContextEntities()){
            mermaidNodes.add(new MermaidNode(entity.getEntityName().getName()));
        }
        //create references here

        // Unsure if this is needed, shouldn't bounded context already have these references in place?
//        //set entity reference
//        for (Entity e: boundedContext.getBoundedContextEntities()
//             ) {
//            for (Field f: e.getFields()
//                 ) {
//                List<Entity> ope =
//                        boundedContext.getBoundedContextEntities().stream().filter(n -> n.getEntityName().getName().equals(f.getType())).collect(Collectors.toList());
//                if (ope.size() > 0){
//                    f.setEntityRefName(ope.get(0).getEntityName().getName());
//                    f.setReference(true);
//                    //f.setCollection(true);
//                }
//
//            }
//        }

        for (Entity entity: boundedContext.getBoundedContextEntities()
             ) {
            for (Field field: entity.getFields()
                 ) {
                if (field.isReference()){
                    List<MermaidEdge> edges = mermaidEdges.stream().filter(n -> n.exists(field.getType(), entity.getEntityName().getName())).collect(Collectors.toList());
                    // no edge yet between entities
                    if (edges.size() == 0) {
                        // no edge yet between these entities, field is not a collection
                        if (!field.isCollection()) {
                            mermaidEdges.add(new MermaidEdge(entity.getEntityName().getName(), field.getType(), "", false, "*", "1"));
                        }
                        // no edge yet, field is collection
                        else {
                            mermaidEdges.add(new MermaidEdge(entity.getEntityName().getName(), field.getType(), "", false, "*", "*"));
                        }
                    }
                    // edge already exists
                    else {
                        mermaidEdges.stream().filter(n -> n.exists(field.getType(), entity.getEntityName().getName())).forEach(e -> {
                            if (e.getFrom().equals(field.getType())) {
                                e.setBidirectional(true);
                                if (!field.isCollection()) {
                                    e.setFromCardinality("1");
                                }
                            }
                        });
                    }
                }
            }
        }
        MermaidGraph mermaidGraph = new MermaidGraph(mermaidNodes, mermaidEdges, boundedContext.getBoundedContextEntities());

        return mermaidGraph;

    }
}
