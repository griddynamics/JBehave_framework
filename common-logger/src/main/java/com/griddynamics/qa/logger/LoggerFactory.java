/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.logger;

import com.griddynamics.qa.tools.resources.ResourceUtil;
import org.apache.log4j.*;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This logger factory class was designed to provide the ability to create a separate
 * log file for every JBehave story
 *
 * @author ybaturina
 * @author yhraichonak
 * @author abaranouski
 */

public class LoggerFactory {

    private static final String CONFIG_FILE_NAME = "log4j.properties";
    private static final String PATTERN = "%d %p (%t) (%F:%L) - %m%n";
    private static final String GLOBAL_NAME = "global";
    private final static String PROJECT_BUILD_DIRECTORY = "project.build.directory";
    private final static String USER_DIRECTORY = "user.dir";

    private static String outputDirectory = System.getProperty(PROJECT_BUILD_DIRECTORY) == null ?
            new StringBuilder(System.getProperty(USER_DIRECTORY)).append(File.separator).append("jbehave").append(File.separator).toString() :
            new StringBuilder(System.getProperty(PROJECT_BUILD_DIRECTORY)).append(File.separator).append("jbehave").append(File.separator).toString();
    /**
     * The name of the log file which contains logs from all JBehave tests
     */
    private static String globalLoggerFile = new StringBuilder(outputDirectory).append("main_")
            .append(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).append(".log").toString();
    private static FileAppender globalFileAppender = getGlobalAppender();

    private static ThreadLocal<Logger> logger = newThreadLocalLogger();
    private static PrintStream printStream = null;


    /**
     * Method returns PrintStream for the global log file
     *
     * @return
     */
    public static PrintStream getGlobalFileStream() {
        if ((printStream) == null || (printStream.checkError())) {
            try {
                printStream = new PrintStream(new FileOutputStream(globalLoggerFile, true));
            } catch (FileNotFoundException e) {
                getLogger().fatal("Global logger file '" + globalLoggerFile + "' could not be opened");
            }
        }
        return printStream;
    }

    /**
     * @return logger instance for the current thread
     */
    public static Logger getLogger() {
        return logger.get();
    }

    /**
     * Creates logger instance for the thread
     *
     * @param fileName - name of the log file
     */
    public static void addThreadLogger(String fileName) {
        FileAppender threadAppender;
        String threadFileName = outputDirectory + fileName;
        try {
            threadAppender = new FileAppender(new PatternLayout(PATTERN),
                    threadFileName, false);
            threadAppender.setName(fileName);
            threadAppender.activateOptions();
            Enumeration appenders = logger.get().getAllAppenders();
            do {
                Appender app;
                try {
                    app = (Appender) appenders.nextElement();
                } catch (NoSuchElementException e) {
                    break;
                }
                if (app.getName().contains("log") && !app.getName().contains(GLOBAL_NAME)) {
                    logger.get().removeAppender(app.getName());
                }
            }
            while (appenders.hasMoreElements());
            logger.get().addAppender(threadAppender);
        } catch (IOException e1) {
            logger.get().fatal("Local thread logger file '" + fileName + "' could not be opened");
        }
    }

    /**
     * @return FileAppender for the global log file
     */
    private static FileAppender getGlobalAppender() {
        try {
            FileAppender appender = new FileAppender(new PatternLayout(PATTERN),
                    globalLoggerFile, false);
            appender.setName(GLOBAL_NAME);
            appender.activateOptions();
            return appender;
        } catch (IOException e) {
            logger.get().fatal("Global logger file '" + globalLoggerFile + "' could not be opened");
        }
        return null;
    }

    private static ThreadLocal<Logger> newThreadLocalLogger() {
        return new ThreadLocal<Logger>() {
            @Override
            protected Logger initialValue() {
                configureLogger();
                Logger globalLogger = Logger.getLogger("Logger for thread " + Thread.currentThread().getName());
                if (globalFileAppender != null) {
                    globalLogger.addAppender(globalFileAppender);
                }
                return globalLogger;
            }
        };
    }

    /**
     * Sets logger properties from the file {@value #CONFIG_FILE_NAME}
     */
    private static void configureLogger() {
        URL fileUrl = ResourceUtil.getResourceUrl(CONFIG_FILE_NAME);
        PropertyConfigurator.configure(fileUrl);
    }

}
