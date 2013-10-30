package com.griddynamics.qa.stubs.soapcommon.service.implementation.data;

/**
 * @author ybaturina
 */
public interface HomePageData {
    public final static String RELATIVE_URL = "<a href=\"./%s\">%s</a>";
    public final static String RELATIVE_URL_DESCRIPTION = RELATIVE_URL + " - %s<br/>";

    public final static String SET_AVAILABLE_TRUE = ServiceData.SET_AVAILABILITY_URL + "?" + ServiceData.SET_AVAILABILITY_PARAM + "=true";
    public final static String SET_AVAILABLE_FALSE = ServiceData.SET_AVAILABILITY_URL + "?" + ServiceData.SET_AVAILABILITY_PARAM + "=false";

    public final static String SET_RESPONSE_TIME = ServiceData.SET_RESPONSE_TIME_URL + "?" + ServiceData.SET_RESPONSE_TIME_PARAM + "=0";



    public final static String HOME_PAGE_CONTENT = "This stub instance works fine.<br/><br/>" +
            "<b>Available urls:</b><br/>" +
            String.format(RELATIVE_URL_DESCRIPTION, SET_AVAILABLE_TRUE, SET_AVAILABLE_TRUE, "set available = true") +
            String.format(RELATIVE_URL_DESCRIPTION, SET_AVAILABLE_FALSE, SET_AVAILABLE_FALSE, "set available = false") +

            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.STUB_SERVICE_URL, ServiceData.STUB_SERVICE_URL, "stub service endpoint") +

            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.CLEAR_HISTORY_URL, ServiceData.CLEAR_HISTORY_URL, "clear stub history") +
            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.CLEAR_DATA_URL, ServiceData.CLEAR_DATA_URL, "clear data loaded to stub") +
            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.CLEAR_ALL_URL, ServiceData.CLEAR_ALL_URL, "clear both history and loaded to stub data") +
            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.HISTORY_URL, ServiceData.HISTORY_URL, "show history") +
            String.format(RELATIVE_URL_DESCRIPTION, SET_RESPONSE_TIME, SET_RESPONSE_TIME, "set response time to 0") +
            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.POPULATE_WITH_FILE_URL, ServiceData.POPULATE_WITH_FILE_URL, "populate with file") +
            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.STUB_LOG_URL, ServiceData.STUB_LOG_URL, "stub log") +
            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.CLEAR_LOG_URL, ServiceData.CLEAR_LOG_URL, "clear stub log") +
            String.format(RELATIVE_URL_DESCRIPTION, ServiceData.VERSION_URL, ServiceData.VERSION_URL, "stub version");
}
