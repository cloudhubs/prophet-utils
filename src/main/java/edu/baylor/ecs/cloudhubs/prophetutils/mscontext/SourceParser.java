package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.*;
import edu.baylor.ecs.cloudhubs.prophetutils.directories.DirectoryUtils;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.model.RestFlow;
import edu.baylor.ecs.cloudhubs.radsource.service.RadSourceService;

import java.io.IOException;
import java.util.*;

public class SourceParser {
    private final RadSourceService radSourceService;

    public SourceParser() {
        this.radSourceService = new RadSourceService();
    }

    public MsModel createMsModel(List<String> pathToMsRoots) throws IOException {
        List<RestFlow> restFlows = getRestFlows(pathToMsRoots);

        Set<MsEdge> msEdges = new HashSet<>();
        Map<String, MsNode> nodeMap = new HashMap<>();

        for (RestFlow restFlow : restFlows) {
            String toKey = DirectoryUtils.getDirectoryNameFromPath(restFlow.getEndpoint().getMsRoot());
            String fromKey = DirectoryUtils.getDirectoryNameFromPath(restFlow.getClient().getMsRoot());

            nodeMap.putIfAbsent(toKey, new MsNode(toKey));
            nodeMap.putIfAbsent(fromKey, new MsNode(fromKey));

            MsNode toNode = nodeMap.get(toKey);
            MsNode fromNode = nodeMap.get(fromKey);

            MsLabel msLabel = new MsLabel();
            msLabel.setType(MsLabelType.valueOf(restFlow.getEndpoint().getHttpMethod()));
            msLabel.setArgument(restFlow.getEndpoint().getArguments());
            msLabel.setMsReturn(restFlow.getEndpoint().getReturnType());
            msLabel.setEndpointFunction(restFlow.getEndpoint().getParentMethod());

            MsEdge msEdge = new MsEdge();
            msEdge.setTo(toNode);
            msEdge.setFrom(fromNode);
            msEdge.setLabel(msLabel);

            msEdges.add(msEdge);
        }

        // add all nodes from map to list
        Set<MsNode> msNodes = new HashSet<>(nodeMap.values());

        // construct MsModel
        MsModel msModel = new MsModel();
        msModel.setNodes(msNodes);
        msModel.setEdges(msEdges);

        return msModel;
    }

    private List<RestFlow> getRestFlows(List<String> pathToMsRoots) throws IOException {
        //ADDED './' JUST TO GET TO COMPILE FOR TESTING. REFACTOR LATER
        RadSourceRequestContext radSourceRequestContext = new RadSourceRequestContext(pathToMsRoots, "./");
        return this.radSourceService.generateRadSourceResponseContext(radSourceRequestContext).getRestFlows();
    }

}
