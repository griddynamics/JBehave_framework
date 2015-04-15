/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.ui.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Interface with common constants used in timeouts
 * @author ybaturina
 */
public interface TimeoutConstants {
    public static String TIMEOUT = "waiter.timeout.sec";
    public static int DEFAULT_TIMEOUT_IN_SECONDS = StringUtils.isEmpty(System.getProperty(TIMEOUT)) ? 5 : Integer.valueOf(System.getProperty(TIMEOUT));
    public final static int WAIT_LOAD_TIMEOUT_IN_MS = 500;
}
