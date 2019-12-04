package edu.baylor.ecs.cloudhubs.prophetutils.adapter;

import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidEdge;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidGraph;
import edu.baylor.ecs.cloudhubs.prophetdto.mermaid.MermaidNode;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Entity;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bounded Context -> Nodes and edges
 */
public class EntityGraphAdapter {

    public static MermaidGraph getMermaidGraph(BoundedContext boundedContext){
        List<MermaidNode> mermaidNodes = new ArrayList<>();
        List<MermaidEdge> mermaidEdges = new ArrayList<>();
        for (Entity entity: boundedContext.getBoundedContextEntities()){
            mermaidNodes.add(new MermaidNode(entity.getEntityName()));
        }
        //create references here

        //set entity reference
        for (Entity e: boundedContext.getBoundedContextEntities()
             ) {
            for (Field f: e.getFields()
                 ) {
                List<Entity> ope =
                        boundedContext.getBoundedContextEntities().stream().filter(n -> n.getEntityName().equals(f.getType())).collect(Collectors.toList());
                if (ope.size() > 0){
                    f.setEntityReference(ope.get(0));
                    f.setReference(true);
                    f.setCollection(true);
                }

            }
        }

        for (Entity entity: boundedContext.getBoundedContextEntities()
             ) {
            for (Field field: entity.getFields()
                 ) {
                if (field.isReference()){
                    System.out.println("is filed");
                    if (mermaidEdges.stream().filter(n -> n.exists(field.getType(), entity.getEntityName())).collect(Collectors.toList()).size() == 0){
                        mermaidEdges.add(new MermaidEdge(field.getType(), entity.getEntityName()));
                    }
                }
            }
        }
        MermaidGraph mermaidGraph = new MermaidGraph(mermaidNodes, mermaidEdges, boundedContext.getBoundedContextEntities());

        return mermaidGraph;

    }
}
