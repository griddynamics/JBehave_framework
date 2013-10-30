package com.griddynamics.qa.stubs.tools.logic;

import com.griddynamics.qa.tools.resources.FileCommonTools;
import com.griddynamics.qa.tools.rest.TestRequest;

import java.io.IOException;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: ybaturina
 * Date: 10/11/13
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SoapStubCommonLogic {

    protected final static String COMMON_DATA_PATH = "/com/griddynamics/data/stubs/";
    protected static final String CLEAR_ALL_RESPONSE = "All information was removed from the stub.";

    protected String stubResetUrl;
    protected String stubLoadDataUrl;
    protected String stubDataPath;
    protected String stubName;

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
            assertEquals("File was not loaded, HTTP status code is ", TestRequest.HTTP_OK, request.getResponse() == null ? "Unable to connect" : request.getStatusCode());
        }

    }

    private String[] getTestFilePaths(String[] fileNames) {
        String[] filePaths = new String[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            String filePath = stubDataPath + fileNames[i];
            if (getClass().getResource(filePath) == null) {
                assertTrue("File with path " + filePath + " that should be loaded to stub " +
                        stubName + " does not exist", false);
            }
            filePaths[i] = filePath;
        }
        return filePaths;
    }

    public void invokeCleanStubMethod() {
        TestRequest request = new TestRequest(stubResetUrl);
        String response = request.getResponseAsString();
        assertEquals("Stub returns <" + CLEAR_ALL_RESPONSE + "> in case when it was successfully cleared\n", CLEAR_ALL_RESPONSE, response);
    }
}
