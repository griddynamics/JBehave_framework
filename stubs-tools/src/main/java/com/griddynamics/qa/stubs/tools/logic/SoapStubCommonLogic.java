package com.griddynamics.qa.stubs.tools.logic;

import com.griddynamics.qa.tools.resources.FileCommonTools;
import com.griddynamics.qa.tools.rest.TestRequest;

import java.io.IOException;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Class containing methods for working with SOAP stub
 *
 * @author ybaturina
 */
public abstract class SoapStubCommonLogic {

    protected final static String COMMON_DATA_PATH = "/com/griddynamics/data/stubs/";
    protected static final String CLEAR_ALL_RESPONSE = "All information was removed from the stub.";

    protected String stubResetUrl;
    protected String stubLoadDataUrl;
    protected String stubDataPath;
    protected String stubName;

    /**
     * Loads content of files into the SOAP stub
     * The files should contain request and responses which can pe processed by stub
     * @param filenames - list of file names divided by comma sign
     */
    public void invokeLoadDataToStubMethod(String filenames) {
        String[] filePaths = getTestFilePaths(filenames.split(","));

        TestRequest request = new TestRequest(stubLoadDataUrl);
        getLogger().info("Url to load is " + stubLoadDataUrl);

        for (int i = 0; i < filePaths.length; i++) {
            getLogger().info("File loading started: " + filePaths[i]);
            try {
                request.post(FileCommonTools.readFromFile(filePaths[i]));
            } catch (IOException e) {
                getLogger().error("Couldn't load data from file, reason: " + e.getMessage());
            }
            assertEquals("[ERROR] File was not loaded, HTTP status code is ", TestRequest.HTTP_OK,
                    request.getResponse() == null ? "Unable to connect" : request.getStatusCode());
        }

    }

    /**
     * Returns real paths of files to be processed by stub,
     * the files should be located in {@link #stubDataPath} folder
     * @param fileNames - list of file names divided by comma sign
     * @return
     */
    private String[] getTestFilePaths(String[] fileNames) {
        String[] filePaths = new String[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            String filePath = stubDataPath + fileNames[i];
            assertThat("[ERROR] File with path " + filePath + " that should be loaded to stub " +
                    stubName + " does not exist", getClass().getResource(filePath), notNullValue());
            filePaths[i] = filePath;
        }
        return filePaths;
    }

    /**
     * Clears all the data out from the stub
     */
    public void invokeCleanStubMethod() {
        TestRequest request = new TestRequest(stubResetUrl);
        String response = request.getResponseAsString();
        assertThat("Stub returns <" + CLEAR_ALL_RESPONSE + "> in case when it was successfully cleared\n", response,
                is(CLEAR_ALL_RESPONSE));
    }
}
