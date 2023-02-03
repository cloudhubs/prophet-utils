package edu.baylor.ecs.cloudhubs.prophetutils.mscontext;

import edu.baylor.ecs.cloudhubs.prophetdto.mscontext.MsModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
class SourceParserTest {

    private static SourceParser sourceParser;

    @BeforeAll
    static void setUp() {
        sourceParser = new SourceParser();
    }

    @Test
    void createMsModel() throws IOException {
        List<String> pathToMsRoots = Arrays.asList(
                "./msJar/tms/cms/",
                "./msJar/tms/ems/",
                "./msJar/tms/qms/"
        );

        MsModel msModel = sourceParser.createMsModel(pathToMsRoots);
        msModel.getNodes().forEach(e -> log.info(e.toString()));
        msModel.getEdges().forEach(e -> log.info(e.toString()));
    }
}