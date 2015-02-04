/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.stubs.soapcommon.service.implementation.data;

/**
 * @author ybaturina
 */
public interface ServiceData {
    public static final String HOME_PAGE_URL = "";

    public static final String SET_AVAILABILITY_URL = "setAvailability";
    public static final String SET_AVAILABILITY_PARAM = "available";
    public static final String SET_AVAILABILITY_RESPONSE = "Set available = ";

    public static final String STUB_SERVICE_URL = "service";

    public static final String CLEAR_HISTORY_URL = "clearHistory";
    public static final String CLEAR_HISTORY_RESPONSE = "Cleared";

    public static final String CLEAR_DATA_URL = "clearData";
    public static final String CLEAR_DATA_RESPONSE = "Stub's data was removed.";

    public static final String CLEAR_ALL_URL = "clear";
    public static final String CLEAR_ALL_RESPONSE = "All information was removed from the stub.";

    public static final String HISTORY_URL = "history";

    public static final String SET_RESPONSE_TIME_URL = "setResponseTime";
    public static final String SET_RESPONSE_TIME_PARAM = "time";
    public static final String SET_RESPONSE_TIME_RESPONSE = "Response time is set to ";

    public static final String POPULATE_WITH_FILE_URL = "populateWithFile";
    public static final String POPULATE_WITH_FILE_RESPONSE = "Filled with data from file\n";
    public static final String POPULATE_REQUEST_DATA_URL = "populateRequestData";
    public static final String POPULATE_REQUEST_DATA_RESPONSE = "Request data filled with data from file\n";

    public static final String STUB_LOG_URL = "log";
    public static final String STUB_LOG_FILE = "stubs-soap-common.log";

    public static final String CLEAR_LOG_URL = "clearLog";
    public static final String CLEAR_LOG_RESPONSE = "Log is cleared";

    public static final String VERSION_URL = "version";
}
