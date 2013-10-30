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
 * @author ybaturina
 */

public class LoggerFactory {

    private static final String CONFIG_FILE_NAME = "log4j.properties";
    private static final String PATTERN = "%d %p (%t) (%F:%L) - %m%n";
    private static final String GLOBAL_NAME = "global";
    private final static String PROJECT_BUILD_DIRECTORY = "project.build.directory";
    private final static String USER_DIRECTORY = "user.dir";

    private static String outputDirectory = System.getProperty(PROJECT_BUILD_DIRECTORY)== null ?
            new StringBuilder(System.getProperty(USER_DIRECTORY)).append(File.separator).append("jbehave").append(File.separator).toString() :
            new StringBuilder(System.getProperty(PROJECT_BUILD_DIRECTORY)).append(File.separator).append("jbehave").append(File.separator).toString();
    private static String globalLoggerFile = outputDirectory + "main_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".log";
    private static FileAppender globalFileAppender = getGlobalAppender();

    private static ThreadLocal<Logger> logger = newThreadLocalLogger();
    private static PrintStream printStream = null;


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

    public static Logger getLogger() {
        return logger.get();
    }

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

    private static void configureLogger() {
        URL fileUrl = ResourceUtil.getResourceUrl(CONFIG_FILE_NAME);
        PropertyConfigurator.configure(fileUrl);
    }

}
