/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.framework.logger;

import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.reporters.TxtOutput;
import org.jbehave.core.steps.StepCreator;

import java.io.PrintStream;

import static com.griddynamics.qa.logger.LoggerFactory.getGlobalFileStream;
import static com.griddynamics.qa.logger.LoggerFactory.getLogger;

/**
 * This custom output format was created to forward Jbehave core logs to logger to
 * implement per-story logging feature in multi-thread environment and convert all output to common format.
 *
 * @author ybaturina
 * @author yhraichonak
 */
public class GlobalLoggerOutput extends TxtOutput {

    public static final org.jbehave.core.reporters.Format GLOBAL_LOGGER_OUTPUT = new GlobalLoggerOutputFormat();
    final static String FAILED_MESSAGE = "FAILED";
    final static String ASSERTION_ERROR = "AssertionError";

    public GlobalLoggerOutput(PrintStream output) {
        super(output);
    }

    /**
     * This method was overridden to log story-specific jbehave logs
     */
    @Override
    protected void print(String text) {
        StringBuilder trimedText = new StringBuilder(text.replace(StepCreator.PARAMETER_TABLE_START, "")
                .replace(StepCreator.PARAMETER_TABLE_END, ""));
        if (trimedText.indexOf("\n") == 0) {
            trimedText.replace(0, 1, "");
        }
        if ((trimedText.lastIndexOf("\n") == trimedText.length() - 1) && (trimedText.length()>0)) {
            trimedText.replace(trimedText.length() - 1, trimedText.length(), "");
        }
        if (trimedText.length() == 0) {
            return;
        }
        if (text.contains(ASSERTION_ERROR) || text.contains(FAILED_MESSAGE))
            getLogger().error(trimedText.toString());
        else {
            getLogger().info(trimedText.toString());
        }

    }

    private static class GlobalLoggerOutputFormat extends org.jbehave.core.reporters.Format {

        public GlobalLoggerOutputFormat() {
            super(GlobalLoggerOutput.class.getName());
        }

        public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder) {
            return new GlobalLoggerOutput(getGlobalFileStream())
                    .doReportFailureTrace(storyReporterBuilder.reportFailureTrace())
                    .doCompressFailureTrace(storyReporterBuilder.compressFailureTrace());
        }
    }
}
