package com.griddynamics.qa.stubs.soapcommon.service.implementation;

/**
 * The class containing response data set.
 * @author ybaturina
 * @author lzakharova
 */
public class ResponseData {

    private String responseData;

    /**
     * Creates instance with {@link ResponseData}
     * @param response
     */
    public ResponseData(String response) {
        responseData = response;
    }

    /**
     * Returns the response data
     */
    public String getResponseData() {
        return responseData;
    }
}
