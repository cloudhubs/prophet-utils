package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.*;
import edu.baylor.ecs.cloudhubs.prophetdto.pyparser.msapi.PyMs;
import edu.baylor.ecs.cloudhubs.prophetdto.pyparser.msapi.PyMsSystem;
import edu.baylor.ecs.cloudhubs.prophetdto.pyparser.msapi.PyPoint;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PyParser {
    private Map<MsNode, Set<PyPoint>> restNodes = new HashMap<>();
    private Map<MsNode, Set<PyPoint>> apiNodes = new HashMap<>();

    public MsModel createMsModel(PyMsSystem system) {
        MsModel msModel = new MsModel();

        for (PyMs microservice : system.getMicroservices()) {
            MsNode node = new MsNode();
            node.setLabel(microservice.getName());
            apiNodes.put(node, new HashSet<>());
            restNodes.put(node, new HashSet<>());

            for (PyPoint api : microservice.getApis()) {
                Set<PyPoint> points = apiNodes.get(node);
                points.add(api);
                apiNodes.put(node, points);
            }

            for (PyPoint rest : microservice.getRests()) {
                Set<PyPoint> points = restNodes.get(node);
                points.add(rest);
                restNodes.put(node, points);
            }

            msModel.getNodes().add(node);
        }

        // set the edges
        for (MsNode node1 : msModel.getNodes()) {
            for (MsNode node2 : msModel.getNodes()) {
                if (node1.equals(node2)) continue;
                Pair<PyPoint, PyPoint> connection = getConnection(node1, node2);
                if (connection != null) {
                    MsEdge edge = createEdge(node1, node2, connection);
                    msModel.getEdges().add(edge);
                }
            }
        }

        return msModel;
    }

    // checks if there is a connection from node1 to node2
    // if there is a exit path from node 1 -> node 2
    private Pair<PyPoint, PyPoint> getConnection(MsNode node1, MsNode node2) {
        Set<PyPoint> restPoints = restNodes.get(node1);
        Set<PyPoint> apiPoints = apiNodes.get(node2);

        // match the two sets
        for (PyPoint restPoint : restPoints) {
            for (PyPoint apiPoint : apiPoints) {
                if (restPoint.getPath().equals(apiPoint.getPath())) {
                    return new Pair<>(restPoint, apiPoint);
                }

            }
        }

        return null;
    }

    // creates an edge from node1 to node2
    private MsEdge createEdge(MsNode node1, MsNode node2, Pair<PyPoint, PyPoint> connection) {
        MsEdge edge = new MsEdge();
        edge.setFrom(node1);
        edge.setTo(node2);

        MsLabel label = new MsLabel();
        label.setArgument(connection.getKey().getPayload().get(0).getType());
        label.setMsReturn(connection.getValue().getResponse().getType());
        label.setType(getRestType(connection.getKey().getName()));

        edge.setLabel(label);

        return edge;
    }

    private MsLabelType getRestType(String name) {
        switch (name) {
            case "post":
                return MsLabelType.POST;
            case "get":
                return MsLabelType.GET;
            case "delete":
                return MsLabelType.DELETE;
            case "put":
                return MsLabelType.PUT;
            default:
                throw new IllegalArgumentException("Illegal argument for getRestType");

        }
    }
}
