package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.RadResponseContext;
import edu.baylor.ecs.cloudhubs.rad.service.RestDiscoveryService;

// TODO: how to initialize restDiscoveryService without Autowire
public class BytecodeParser {
    private final RestDiscoveryService restDiscoveryService;

    public BytecodeParser() {
        // this.restDiscoveryService = new RestDiscoveryService();
        this.restDiscoveryService = null;
    }

    public MsModel createMsModel(String organizationPath, String pathToCompiledJars) {
        // no output path i.e. don't output gv file
        RadRequestContext request = new RadRequestContext(pathToCompiledJars, organizationPath, null);
        RadResponseContext radResponseContext = this.restDiscoveryService.generateRadResponseContext(request);

        // TODO: convert and return MsModel
        return null;
    }
}
