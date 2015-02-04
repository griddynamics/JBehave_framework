/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
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

    public final static String DYN_ADMIN_RELATIVE_PATH = "/dyn/admin/nucleus";

    public static Map<String, String> SERVICE_URLS = new HashMap<String, String>();

    public static final int ITEM_LOAD_TIMEOUT = 5000;
}
