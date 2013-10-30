package com.griddynamics.qa.stubs.tools.data;

import com.griddynamics.qa.stubs.tools.properties.StubsProperties;

/**
 * @author ybaturina
 */
public interface StubServiceData {
    public String BASE_URL = "http://" + StubsProperties.getStubServiceIp() +
            ":" + StubsProperties.getStubServicePort();

    static final String STUB_NAME = "Common SOAP Stub";

    static final String STUB_RELATIVE_PATH = StubsProperties.getStubServiceRelativePath();

    static final String STUB_RESET_URL = "/"+STUB_RELATIVE_PATH+"/clear";
    static final String LOAD_DATA_URL = "/"+STUB_RELATIVE_PATH+"/populateWithFile";

}
