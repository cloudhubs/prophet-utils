package edu.baylor.ecs.cloudhubs.prophetutils.jparser;

import edu.baylor.ecs.ciljssa.component.context.AnalysisContext;
import edu.baylor.ecs.ciljssa.component.impl.*;
import edu.baylor.ecs.ciljssa.factory.context.AnalysisContextFactory;
import edu.baylor.ecs.ciljssa.factory.directory.DirectoryFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

/**
 * Non-static methods because it requires the initialization of the appropriate factories for JParser.
 *
 * Similarly, when running these factories perhaps multiple times on a single session, you should use the same
 * factories to avoid duplicate IDs being generated. This is fixed by just keeping the factories loaded.
 *
 * Get instance of this singleton via getInstance() and call desired methods from there.
 *
 * @author Jonathan Simmons
 *
 * TODO: - Add functionality for mapping a single module to a SystemContext
 *       - Make an efficient method for populating a SystemContext for a single file
 */
public class JParserUtils {

    /**
     * Version of JParser currently being used
     */
    public static final int JPARSER_VERSION = 4;

    /**
     * Version of the Prophet-DTO being used
     */
    public static final int PROPHET_DTO_VERSION = 1;

    private static JParserUtils INSTANCE;

    private static DirectoryFactory directoryFactory;
    private static AnalysisContextFactory contextFactory;

    private static final int DIRECTORY = 0;
    private static final int FILE = 1;


    /**
     * Singleton instance getter
     * @return gets this instance of systemcontextparser
     */
    public static JParserUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JParserUtils();
        }
        return INSTANCE;
    }

    /**
     * Private constructor for singleton
     */
    private JParserUtils() {
        directoryFactory = new DirectoryFactory();
        contextFactory = new AnalysisContextFactory();
    }

    /**
     * Create an analysis context object directly via some string path.
     * @param path Path to directory - not to file.
     * @return an AnalysisContext object
     */
    public AnalysisContext createAnalysisContextFromDirectory(String path) {
        try {
            validate(path, DIRECTORY);
            DirectoryComponent directory = (DirectoryComponent) directoryFactory.createDirectoryGraph(path);
            return contextFactory.createAnalysisContextFromDirectoryGraph(directory);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not create AnalysisContext");
        }
        return  null;
    }

    /**
     * Create an analysis context object directly for a given single file. Still populates whole context object which
     * has large overhead. If all you need is information regarding a single class or single module without any metadata,
     * this is not the best way.
     * @param file File to parse
     * @return an AnalysisContext object for the file
     */
    public AnalysisContext createAnalysisContextFromFile(File file) {
        try {
            validate(file.getPath(), FILE);
            DirectoryComponent directory = (DirectoryComponent) directoryFactory.createDirectoryGraphOfFile(file);
            return contextFactory.createAnalysisContextFromDirectoryGraph(directory);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not create AnalysisContext");
        }
        return null;
    }

    /**
     * Create an analysis context object directly for a given single file. Still populates whole context object which
     * has large overhead. If all you need is information regarding a single class or single module without any metadata,
     * this is not the best way.
     * @param filePath Path of the file to parse
     * @return an AnalysisContext object for the file
     */
    public AnalysisContext createAnalysisContextFromFile(String filePath) {
        try{
            validate(filePath, FILE);
            DirectoryComponent directory = (DirectoryComponent) directoryFactory
                    .createDirectoryGraphOfFile(new File(filePath));
            return contextFactory.createAnalysisContextFromDirectoryGraph(directory);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not create AnalysisContext");
        }
        return null;
    }



    /**
     * Validate that the input is correct before trying to run JParser on it. Protects against possible crash or just
     * overall overhead.
     * @param path Path of file or object
     * @param type What to test for
     */
    private void validate(String path, int type) throws NotDirectoryException, FileNotFoundException {
        if (type == DIRECTORY) {
            File file = new File(path);
            if (!file.isDirectory()) {
                throw new NotDirectoryException("Input to JParserUtils was expected to be a directory and is not!");
            }
            if (file.listFiles() == null) {
                throw new NotDirectoryException("The supplied root directory contains no files!");
            }
        } else if (type == FILE) {
            File file = new File(path);
            if (file.isDirectory()) {
                throw new FileNotFoundException("Input to JParserUtils was expected to be a file and it is not!");
            }
            if (!file.exists()) {
                throw new FileNotFoundException("File supplied to JParserUtils does not exist!");
            }
        }
    }

}
