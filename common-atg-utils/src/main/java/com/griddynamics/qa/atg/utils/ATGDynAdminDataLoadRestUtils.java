package com.griddynamics.qa.atg.utils;

import com.griddynamics.qa.tools.resources.FileCommonTools;
import com.griddynamics.qa.tools.rest.TestRequest;
import com.jayway.restassured.RestAssured;
import com.griddynamics.qa.atg.utils.data.ATGDataLoadRestUtilsData;
import com.griddynamics.qa.atg.utils.properties.ATGDynAdminProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.preemptive;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Class contains utility methods for loading test data into ATG repositories
 *
 * @author ybaturina
 */
public class ATGDynAdminDataLoadRestUtils implements ATGDataLoadRestUtilsData {

    protected static final String DYN_ADMIN_USER = ATGDynAdminProperties.getDynAdminUser();
    protected static final String DYN_ADMIN_PASSW = ATGDynAdminProperties.getDynAdminPassw();
    protected static final String DYN_ADMIN_URL = ATGDynAdminProperties.getDynadminPort().isEmpty() ? "http://" + ATGDynAdminProperties.getDynadminIp() + "/dyn/admin/nucleus"
            : "http://" + ATGDynAdminProperties.getDynadminIp() + ":" + ATGDynAdminProperties.getDynadminPort() + "/dyn/admin/nucleus";
    protected static TestRequest request = getTestRequest();
    protected static String fullRestUrl;

    /**
     * Method initializes parameters of REST connection to dyn/admin
     * @return
     */
    protected static TestRequest getTestRequest() {
        RestAssured.baseURI = "http://" + ATGDynAdminProperties.getDynadminIp();
        RestAssured.port = Integer.valueOf(ATGDynAdminProperties.getDynadminPort());
        RestAssured.authentication = preemptive().basic(DYN_ADMIN_USER, DYN_ADMIN_PASSW);
        return new TestRequest();
    }

    /**
     * Method sets full repository REST path
     * @param repoName - name of service
     */
    protected static void setFullRestURL(String repoName) {
        fullRestUrl = DYN_ADMIN_URL + SERVICE_URLS.get(repoName);
    }

    /**
     * Method sends content from file to the repository
     * @param resourcePath - path to the resource located on classpath
     * @param repoName - name of repository
     * @throws IOException
     */
    protected static void sendFileContentToRepository(String resourcePath, String repoName) throws IOException {
        sendFileContentToRepository(resourcePath, repoName, ITEM_LOAD_TIMEOUT);
    }

    /**
     * Method sends content from file to the repository and waits for the specified amount of time
     * @param resourcePath - path to the resource located on classpath
     * @param repoName - name of repository
     * @param timeout - amount of time in ms
     * @throws IOException
     */
    protected static void sendFileContentToRepository(String resourcePath, String repoName, int timeout) throws IOException {
        setFullRestURL(repoName);
        request.setUrl(fullRestUrl);
        executeQueryOnRepository(FileCommonTools.readFromFile(resourcePath), timeout);
    }

    /**
     * Method sends content of String to the repository
     * @param query - String with RQL statements
     * @param repoName - name of repository
     */
    protected static void sendQueryToRepository(String query, String repoName) {
        sendQueryToRepository(query, repoName, ITEM_LOAD_TIMEOUT);
    }

    /**
     * Method sends content of String to the repository and waits for the specified amount of time
     * @param query - String with RQL statements
     * @param repoName - name of repository
     * @param timeout - amount of time in ms
     */
    protected static void sendQueryToRepository(String query, String repoName, int timeout) {
        setFullRestURL(repoName);
        request.setUrl(fullRestUrl);
        executeQueryOnRepository(query, timeout);
    }

    /**
     * Method invokes InvalidateCaches() method in repository
     * @param repoName - name of repository
     */
    protected static void invalidateCachesInRepository(String repoName) {
        invalidateCachesInRepository(repoName, ITEM_LOAD_TIMEOUT);
    }

    /**
     * Method invokes InvalidateCaches() method in repository and waits for the specified amount of time
     * @param repoName - name of repository
     * @param timeout - amount of time in ms
     */
    protected static void invalidateCachesInRepository(String repoName, int timeout) {
        setFullRestURL(repoName);
        request.setUrl(fullRestUrl);
        executeOperationOnService(invalidateCachesParamsMap, timeout);
    }

    /**
     * Method executes RQL query statement in repository and waits for the specified amount of time
     * @param query - RQL query statement as String
     * @param timeout - amount of time in ms
     */
    protected static void executeQueryOnRepository(String query, int timeout) {
        if (!query.isEmpty()) {
            Map<String, String> propsMap = new HashMap<String, String>();
            propsMap.put("xmltext", query);
            executeOperationOnService(propsMap, timeout);
        }
    }

    /**
     * Method executes some operation on service using request properties as input parameter
     * and waits for the specified amount of time
     * @param propsMap - properties of REST request
     * @param timeout - amount of time in ms
     */
    protected static void executeOperationOnService(Map<String, String> propsMap, int timeout){
        request.post(propsMap);
        assertThat("[ERROR] Request returned status code " + request.getStatusCode(),
                request.getStatusCode(), equalTo(request.HTTP_OK));
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            assertTrue("[ERROR] Exception occured during operation" + e.getMessage(), false);
        }
    }

}
