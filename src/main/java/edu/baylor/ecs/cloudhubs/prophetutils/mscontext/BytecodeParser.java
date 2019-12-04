package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.*;
import edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.RadResponseContext;
import edu.baylor.ecs.cloudhubs.rad.model.RestFlow;
import edu.baylor.ecs.cloudhubs.rad.service.RestDiscoveryService;

import java.util.*;

// TODO: how to initialize restDiscoveryService without Autowire
public class BytecodeParser {
    private final RestDiscoveryService restDiscoveryService;

    public BytecodeParser() {
        this.restDiscoveryService = new RestDiscoveryService();
    }

    public MsModel createMsModel(String organizationPath, String pathToCompiledJars) {
        List<RestFlow> restFlows = getRestFlows(organizationPath, pathToCompiledJars);

        Set<MsEdge> msEdges = new HashSet<>();
        Map<String, MsNode> nodeMap = new HashMap<>();

        for (RestFlow restFlow : restFlows) {
            String toKey = restFlow.getServers().get(0).getResourcePath();
            String fromKey = restFlow.getResourcePath();

            nodeMap.putIfAbsent(toKey, new MsNode(toKey));
            nodeMap.putIfAbsent(fromKey, new MsNode(fromKey));

            MsNode toNode = nodeMap.get(toKey);
            MsNode fromNode = nodeMap.get(fromKey);

            MsLabel msLabel = new MsLabel();
            msLabel.setType(MsLabelType.valueOf(restFlow.getServers().get(0).getHttpMethod().toString()));
            msLabel.setArgument(restFlow.getServers().get(0).getUrl()); // url instead of argument
            msLabel.setMsReturn(restFlow.getServers().get(0).getReturnType());

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

    private List<RestFlow> getRestFlows(String organizationPath, String pathToCompiledJars) {
        // no output path i.e. don't output gv file
        RadRequestContext request = new RadRequestContext(pathToCompiledJars, organizationPath, null);
        RadResponseContext radResponseContext = this.restDiscoveryService.generateRadResponseContext(request);
        return radResponseContext.getRestFlowContext().getRestFlows();
    }
}
