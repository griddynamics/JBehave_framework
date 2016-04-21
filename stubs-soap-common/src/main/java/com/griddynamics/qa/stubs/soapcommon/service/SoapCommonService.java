/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.stubs.soapcommon.service;

import javax.naming.ServiceUnavailableException;
import java.io.InputStream;
import java.util.List;

/**
 * This interface provides the SOAP stub common methods
 *
 * @author ybaturina
 * @author lzakharova
 */
public interface SoapCommonService {

    /**
     * Returns a response by the required request
     * @param request - the required request
     * @return  String
     * @throws ServiceUnavailableException if happens
     */
    String getCommonResponse(String request) throws ServiceUnavailableException;

    /**
     * Returns home page
     * @return home page
     */
    String getHomePage();

    /**
     * Fills the data set from a file
     * @param inputStream - a stream of the file
     */
    void fillFromFile(InputStream inputStream);

    /**
     * Fills request data from a file
     * @param inputStream - a stream of the file
     */
    void fillRequestDataFromFile(InputStream inputStream);

    /**
     * Adds request/response pair
     * @param request request
     * @param response response
     */
    void addPair(String request, String response);

    /**
     * Cleans all stub content
     */
    void clear();

    /**
     * Returns a history in a List
     * @return history in a List
     */
    List history();

    /**
     * Sets status available or not for a stub
     * @param available available
     */
    void setAvailability(boolean available);

    /**
     * Sets timeout for request
     * @param delta - the timeout
     */
    void setTimeout(int delta);

    /**
     * Clears stub history
     */
    void clearHistory();

    /**
     * Clears loaded into the stub data
     */
    void clearData();
}

