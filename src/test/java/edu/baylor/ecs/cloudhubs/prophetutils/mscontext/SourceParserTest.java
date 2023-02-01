package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
class SourceParserTest {

    private static SourceParser sourceParser;

    @BeforeAll
    static void setUp() {
        sourceParser = new SourceParser();
    }

//    @Test
//    void createMsModel() throws IOException {
//        List<String> pathToMsRoots = Arrays.asList(
//                "C:\\seer-lab\\cil-tms\\tms-cms",
//                "C:\\seer-lab\\cil-tms\\tms-ems",
//                "C:\\seer-lab\\cil-tms\\tms-qms",
//                "C:\\seer-lab\\cil-tms\\tms-ums"
//        );
//
//        MsModel msModel = sourceParser.createMsModel(pathToMsRoots);
//        msModel.getNodes().forEach(e -> log.info(e.toString()));
//        msModel.getEdges().forEach(e -> log.info(e.toString()));
//    }
}