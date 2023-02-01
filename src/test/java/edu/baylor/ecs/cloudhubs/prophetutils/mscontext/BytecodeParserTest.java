package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class BytecodeParserTest {

    private static BytecodeParser bytecodeParser;

    @BeforeAll
    static void setUp() {
        bytecodeParser = new BytecodeParser();
    }

    // TODO: setup later on after CI/CD finished
//    @Test
//    void createMsModel() {
//        String pathToCompiledMicroservices = "C:\\seer-lab\\cil-tms";
//        String organizationPath = "edu/baylor/ecs";
//
//        MsModel msModel = bytecodeParser.createMsModel(organizationPath, pathToCompiledMicroservices);
//        msModel.getNodes().forEach(e -> log.info(e.toString()));
//        msModel.getEdges().forEach(e -> log.info(e.toString()));
//    }
}
