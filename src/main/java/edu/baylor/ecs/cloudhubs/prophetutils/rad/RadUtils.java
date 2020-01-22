package edu.baylor.ecs.cloudhubs.prophetutils.rad;

import edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.RadResponseContext;
import edu.baylor.ecs.cloudhubs.rad.service.RestDiscoveryService;

/**
 * Utils for interacting with RAD tool
 *
 * @author Vincent Bushong
 */
public class RadUtils {

    public static RadResponseContext getResponseContext() {
        RestDiscoveryService discoveryService = new RestDiscoveryService();

        // TODO: call discovery service

        return new RadResponseContext();
    }
}
