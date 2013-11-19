package com.griddynamics.qa.atg.utils.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface contains constants for Data Load utils
 *
 * @author ybaturina
 */
public interface ATGDataLoadRestUtilsData {

    public static Map<String, String> invalidateCachesParamsMap = new HashMap<String, String>() {{
        put("invokeMethod", "invalidateCaches");
        put("submit", "Invoke Method");
    }};

    public static Map<String, String> SERVICE_URLS = new HashMap<String, String>();

    public static final int ITEM_LOAD_TIMEOUT = 5000;
}
