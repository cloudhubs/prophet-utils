package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.*;
import edu.baylor.ecs.cloudhubs.prophetutils.directories.DirectoryUtils;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.model.RestFlow;
import edu.baylor.ecs.cloudhubs.radsource.service.RadSourceService;

import java.io.IOException;
import java.util.*;

import com.google.common.collect.Lists;

public class SourceParser {
    private final RadSourceService radSourceService;

    public SourceParser() {
        this.radSourceService = new RadSourceService();
    }

    public MsModel createMsModel(List<String> pathToMsRoots) throws IOException {
    	return this.createMsModel(pathToMsRoots, null);
    }
    
    public MsModel createMsModel(List<String> pathToMsRoots, String extractedJsonDataFilePath) throws IOException {
        List<RestFlow> restFlows = getRestFlows(pathToMsRoots, extractedJsonDataFilePath);

        Set<MsEdge> msEdges = new HashSet<>();
        Map<String, MsNode> nodeMap = new HashMap<>();
      /// FOR SUBSET
//        String[] subset = new String[] {"ts-auth-service", "ts-ticketinfo-service", "ts-price-service", "ts-config-service", "ts-preserve-service", "ts-train-service", "ts-route-service", "ts-user-service", "ts-security-service", "ts-order-service", "ts-travel-plan-service", "ts-cancel-service", "ts-travel-service", "ts-route-plan-service", "ts-station-service", "ts-contacts-service", "ts-basic-service"};
//        List<String> subsetList = Arrays.asList(subset);

        for (RestFlow restFlow : restFlows) {
            String toKey = restFlow.getEndpoint().getMsRoot();
            String fromKey = restFlow.getClient().getMsRoot();
            if (toKey.contains("/")) {
            	toKey = DirectoryUtils.getDirectoryNameFromPath(restFlow.getEndpoint().getMsRoot());
            }
            
            if (fromKey.contains("/")) {
            	fromKey = DirectoryUtils.getDirectoryNameFromPath(restFlow.getClient().getMsRoot());
            }
            
            /// FOR SUBSET
//            if(!subsetList.contains(toKey) || !subsetList.contains(fromKey)) {
//            	continue;
//            }

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

    private List<RestFlow> getRestFlows(List<String> pathToMsRoots, String extractedJsonDataFilePath) throws IOException {
        RadSourceRequestContext radSourceRequestContext = new RadSourceRequestContext(pathToMsRoots, extractedJsonDataFilePath);
        return this.radSourceService.generateRadSourceResponseContext(radSourceRequestContext).getRestFlows();
    }

}
