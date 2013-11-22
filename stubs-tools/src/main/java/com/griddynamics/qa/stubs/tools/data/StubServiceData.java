package com.griddynamics.qa.stubs.tools.data;

import com.griddynamics.qa.stubs.tools.properties.StubsProperties;

/**
 * Interface containing common String constants used by SOAP stub
 *
 * @author ybaturina
 */
public interface StubServiceData {
    public String BASE_URL = new StringBuilder("http://").append(StubsProperties.getStubServiceIp()).append(":")
            .append(StubsProperties.getStubServicePort()).toString();

    static final String STUB_NAME = "Common SOAP Stub";

    static final String STUB_RELATIVE_PATH = StubsProperties.getStubServiceRelativePath();

    static final String STUB_RESET_URL = new StringBuilder("/").append(STUB_RELATIVE_PATH)
            .append("/clear").toString();
    static final String LOAD_DATA_URL = new StringBuilder("/").append(STUB_RELATIVE_PATH)
            .append("/populateWithFile").toString();

}
