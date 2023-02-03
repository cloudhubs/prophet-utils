package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class BytecodeParserTest {

    private static BytecodeParser bytecodeParser;

    @BeforeAll
    static void setUp() {
        bytecodeParser = new BytecodeParser();
    }
    @Test
    void createMsModel() {
        String pathToCompiledMicroservices = "./msJar/tms/";
        String organizationPath = "edu/baylor/ecs";

        MsModel msModel = bytecodeParser.createMsModel(organizationPath, pathToCompiledMicroservices);
        msModel.getNodes().forEach(e -> log.info(e.toString()));
        msModel.getEdges().forEach(e -> log.info(e.toString()));
    }
}
