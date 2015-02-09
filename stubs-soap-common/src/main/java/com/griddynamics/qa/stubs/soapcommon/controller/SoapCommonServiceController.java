/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.stubs.soapcommon.controller;

import com.griddynamics.qa.stubs.soapcommon.logger.CustomHtmlLayout;
import com.griddynamics.qa.stubs.soapcommon.service.SoapCommonService;
import com.griddynamics.qa.stubs.soapcommon.service.implementation.data.ServiceData;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * This class provides a web controller {@link Controller} for the stub. It uses {@link RequestMapping} for mapping all
 * GET requests and forwards this. The controller listens all SOAP requests on an used port.
 *
 * @author ybaturina
 * @author lzakharova
 */
@Controller
@RequestMapping("/")
public class SoapCommonServiceController implements ServiceData {

    @Autowired
    SoapCommonService service;

    static Logger logger = Logger.getLogger(
            SoapCommonServiceController.class.getName());

    private static FileAppender fileAppender = getFileAppender();

    /**
     * <p>
     * Home Page
     * </p>
     */
    @RequestMapping(HOME_PAGE_URL)
    @ResponseBody
    public String homePage() {
        return service.getHomePage();
    }

    /**
     * Method processes the {@link HttpServletRequest} request and returns
     * the response according to the data loaded into stub
     *
     * @param request - the {@link HttpServletRequest} request
     * @param response - the {@link HttpServletResponse} response
     * @return - the response, which was sending.
     * @throws IOException
     * @throws ServiceUnavailableException
     */
    @RequestMapping(value = STUB_SERVICE_URL, method = RequestMethod.POST)
    @ResponseBody
    public String stubServiceEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceUnavailableException {
        String requestString = readBody(request.getReader());
        return service.getCommonResponse(requestString)+"\n";
    }

    /**
     * This method provides working with trigger, which turns on/off availability for the stub. <br/>
     *
     * @param available if that's false: the stub won't be working.
     * @return the string with a status: the stub's available or not.
     */
    @RequestMapping(SET_AVAILABILITY_URL)
    @ResponseBody
    public String setAvailability(@RequestParam(value = SET_AVAILABILITY_PARAM) boolean available) {
        service.setAvailability(available);
        logger.info("Availability set to " + available);
        return SET_AVAILABILITY_RESPONSE + available;
    }

    /**
     * This method cleans all requests history and returns answer that all information's removed.
     *
     * @return the string "Cleared"
     */
    @RequestMapping(CLEAR_HISTORY_URL)
    @ResponseBody
    public String clearHistory() {
        service.clearHistory();
        logger.info("Stub history cleared");
        return CLEAR_HISTORY_RESPONSE;
    }

    /**
     * The method cleans all loaded information for the stub and returns that it's cleared.
     */
    @RequestMapping(CLEAR_DATA_URL)
    @ResponseBody
    public String clearData() {
        service.clearData();
        logger.info("Loaded to stub data cleared");
        return CLEAR_DATA_RESPONSE;
    }

    /**
     * Cleans all requests for history and all loaded information for the stub and
     * returns a string with message that it's cleared.
     *
     * @return
     */
    @RequestMapping(CLEAR_ALL_URL)
    @ResponseBody
    public String clear() {
        service.clearData();
        service.clearHistory();
        logger.info("Stub history and loaded to stub data cleared");
        return CLEAR_ALL_RESPONSE;
    }

    /**
     * Sets response time.
     *
     * @param delta response value
     * @return the string with answer that it was set.
     */
    @RequestMapping(SET_RESPONSE_TIME_URL)
    @ResponseBody
    public String setResponseTime(@RequestParam(value = SET_RESPONSE_TIME_PARAM) int delta) {
        service.setTimeout(delta);
        logger.info("Response time set to " + delta);
        return SET_RESPONSE_TIME_RESPONSE + delta;
    }

    /**
     * Returns a list with all history responses and requests.
     */
    @RequestMapping(HISTORY_URL)
    @ResponseBody
    public List<String> history() {
        return service.history();
    }

    /**
     * This method loads a file with a stub data in the stub.
     *
     * @param request the {@link HttpServletRequest} request.
     * @return the string with answer that it was filled.
     */
    @RequestMapping(value = POPULATE_WITH_FILE_URL, method = RequestMethod.POST)
    @ResponseBody
    public String populateWithFile(HttpServletRequest request) {
        try {
            String requestString = readBody(request.getReader());
            service.fillFromFile(new ByteArrayInputStream(requestString.getBytes()));
            logger.info("Stub filled with data: " + requestString);

        } catch (IOException e) {
            logger.fatal("Cannot read input file, error happened: " + e.getMessage());
        }
        return POPULATE_WITH_FILE_RESPONSE;
    }

    @RequestMapping(value = POPULATE_REQUEST_DATA_URL, method = RequestMethod.POST)
    @ResponseBody
    public String populateRequestData(HttpServletRequest request) {
        try {
            String requestString = readBody(request.getReader());
            service.fillRequestDataFromFile(new ByteArrayInputStream(requestString.getBytes()));
            logger.info("Stub request types filled with data: " + requestString);

        } catch (IOException e) {
            logger.fatal("Cannot read input file, error happened: " + e.getMessage());
        }
        return POPULATE_REQUEST_DATA_RESPONSE;
    }

    /**
     * Returns stub log file content
     * @param request
     * @return log file content as String
     */
    @RequestMapping(STUB_LOG_URL)
    @ResponseBody
    public String log(HttpServletRequest request) {
        try {
            File log = new File(STUB_LOG_FILE);
            if (log.exists() && log.length() > 0) {
                return new Scanner(log).useDelimiter("\\A").next();
            } else {
                return "";
            }

        } catch (IOException e) {
            logger.fatal("Cannot read file with log: " + STUB_LOG_FILE);
        }
        return null;
    }

    /**
     * Clears stub log file content
     *
     * @return String message indicating whether the log was cleared
     */
    @RequestMapping(CLEAR_LOG_URL)
    @ResponseBody
    public String clearLog() {
        try {
            FileOutputStream eraser = new FileOutputStream(STUB_LOG_FILE);
            try {
                eraser.write("".getBytes());
                eraser.close();
                logger.getRootLogger().removeAllAppenders();
                logger.getRootLogger().addAppender(fileAppender);
            } catch (IOException e) {
                StringBuilder message = new StringBuilder().append("Cannot clear log: ").append(STUB_LOG_FILE).append(" ").append(e.getCause());
                logger.fatal(message.toString());
                return message.toString();
            } finally {
                try {
                    eraser.close();
                } catch (IOException e) {
                    StringBuilder message = new StringBuilder().append("Cannot close log: ").append(STUB_LOG_FILE).append(" ").append(e.getCause());
                    logger.fatal(message.toString());
                    return message.toString();
                }
            }
        } catch (FileNotFoundException e) {
            StringBuilder message = new StringBuilder().append("Cannot find log file: ").append(STUB_LOG_FILE).append(" ").append(e.getCause());
            logger.fatal(message.toString());
            return message.toString();
        }
        return CLEAR_LOG_RESPONSE;
    }

    /**
     *
     * @return SOAP stub version
     */
    @RequestMapping(VERSION_URL)
    @ResponseBody
    public String getVersion() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            return properties.getProperty("application.version");
        } catch (Exception e) {
            logger.fatal("Cannot get webapp version, error happened: " + e.getMessage());
        }
        return "Cannot get webapp version";
    }

    /**
     * Method processes content of stub request
     * @param reader - BufferedReader from HttpServletRequest
     * @return request content as String
     */
    private String readBody(BufferedReader reader) {
        StringBuilder requestData;
        try {
            requestData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestData.append(line);
            }
        } catch (IOException e) {
            logger.fatal("Cannot read string from BufferedReader, error happened: " + e.getMessage());
            return null;
        }
        return requestData.toString();
    }

    /**
     * Method for creating FileAppender used by stub logger
     * @return FileAppender for {@link #STUB_LOG_FILE} log file
     */
    private static FileAppender getFileAppender() {
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile(STUB_LOG_FILE);
        fileAppender.setName("HTML Layout");
        fileAppender.setLayout(new CustomHtmlLayout());
        fileAppender.activateOptions();
        return fileAppender;
    }
}
