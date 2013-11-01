package com.griddynamics.qa.framework.logger;

import org.jbehave.core.embedder.PrintStreamEmbedderMonitor;

import java.io.File;
import java.io.PrintStream;

import static com.griddynamics.qa.framework.converters.StackTraceConverter.getStringFromStackTrace;
import static com.griddynamics.qa.logger.LoggerFactory.addThreadLogger;
import static com.griddynamics.qa.logger.LoggerFactory.getLogger;

/**
 * The class is used to send output of all common jbehave logs in separate files
 *
 * @author ybaturina
 * @author abaranouski
 */
public class CustomPrintStreamEmbedderMonitor extends PrintStreamEmbedderMonitor {

    public CustomPrintStreamEmbedderMonitor(PrintStream output) {
        super(output);
    }

    /**
     * This method was overridden to log jbehave suite-specific logs
     */
    @Override
    public void print(String message) {
        getLogger().info(message);
    }

    /**
     * This method was overridden to log jbehave suite-specific exception
     */
    @Override
    public void printStackTrace(Throwable e) {
        getLogger().error(getStringFromStackTrace(e));
    }

    /**
     * This method was overridden to initialize local-thread Logger to send output to story-specific log file.
     */
    @Override
    public void runningStory(String path) {
        addThreadLogger(new File(path.replace(".story", ".log")).getName());
        getLogger().info("Running story " + path);
    }

}