/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.framework;

import com.griddynamics.qa.mobile.MobileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.ScenarioType;
import org.jbehave.core.failures.PendingStepFound;
import org.jbehave.core.failures.UUIDExceptionWrapper;
import org.jbehave.core.model.OutcomesTable;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.web.selenium.RemoteWebDriverProvider;
import org.jbehave.web.selenium.WebDriverProvider;
import org.jbehave.web.selenium.WebDriverSteps;

import java.io.File;
import java.util.UUID;

import static com.griddynamics.qa.framework.converters.StackTraceConverter.getStringFromStackTrace;
import static com.griddynamics.qa.logger.LoggerFactory.getLogger;

/**
 * Class implements functionality of catching screenshots and additional logging in case of test failure
 *
 * @author mlykosova
 * @author ybaturina
 * @author dcheremushkin
 */
public class CustomWebDriverScreenshotOnFailure extends WebDriverSteps {
    public static final String DEFAULT_SCREENSHOT_PATH_PATTERN = "%s/screenshots/failed-scenario-%s.png";
    public static final String PROPERTY_MOBILE = "mobile";

    protected StoryReporterBuilder reporterBuilder;
    protected final String screenshotPathPattern;

    public CustomWebDriverScreenshotOnFailure(WebDriverProvider driverProvider) {
        this(driverProvider, new StoryReporterBuilder());
    }

    public CustomWebDriverScreenshotOnFailure(WebDriverProvider driverProvider, StoryReporterBuilder reporterBuilder) {
        this(driverProvider, reporterBuilder, DEFAULT_SCREENSHOT_PATH_PATTERN);
    }

    public CustomWebDriverScreenshotOnFailure(WebDriverProvider driverProvider, StoryReporterBuilder reporterBuilder, String screenshotPathPattern) {
        super(driverProvider);
        this.reporterBuilder = reporterBuilder;
        this.screenshotPathPattern = screenshotPathPattern;
    }

    public StoryReporterBuilder getReporterBuilder() {
        return reporterBuilder;
    }

    public void setReporterBuilder(StoryReporterBuilder reporterBuilder) {
        this.reporterBuilder = reporterBuilder;
    }


    /**
     * Method is triggered when the scenario failure happens;
     * In this case:
     *   1) the screenshot of current browser page is made
     *   2) appium's error message is printed to Maven's output [for mobile testing ONLY]
     *
     * @param uuidWrappedFailure uuidWrappedFailure
     * @throws Exception if happened
     */
    @AfterScenario(uponType = ScenarioType.EXAMPLE, uponOutcome = AfterScenario.Outcome.FAILURE)
    public void afterScenarioWithExamplesFailure(UUIDExceptionWrapper uuidWrappedFailure) throws Exception {
        afterScenarioFailure(uuidWrappedFailure);
    }

    @AfterScenario(uponType = ScenarioType.NORMAL, uponOutcome = AfterScenario.Outcome.FAILURE)
    public void afterScenarioFailure(UUIDExceptionWrapper uuidWrappedFailure) throws Exception {
        if (uuidWrappedFailure instanceof PendingStepFound) {
            return; // we don't take screenshots for Pending Steps
        }

        if (StringUtils.isNotBlank(System.getProperty(PROPERTY_MOBILE))) {
            MobileUtils.outputAppiumErrors();
        }

        String screenshotPath = "";

        if (uuidWrappedFailure.getCause() instanceof OutcomesTable.OutcomesFailed){
            screenshotPath = screenshotPath(((OutcomesTable.OutcomesFailed)(uuidWrappedFailure.getCause())).outcomesTable().failureCause().getUUID());
        }
        else {
            screenshotPath = screenshotPath(uuidWrappedFailure.getUUID());
        }
        String currentUrl = "[unknown page URL]";
        try {
            currentUrl = driverProvider.get().getCurrentUrl();
        } catch (Exception e) {
            getLogger().error("Driver can't get URL of the current page");
        }
        boolean savedIt;
        try {
            savedIt = driverProvider.saveScreenshotTo(screenshotPath);
        } catch (RemoteWebDriverProvider.SauceLabsJobHasEnded e) {
            getLogger().error("Screenshot of page '" + currentUrl + "' has **NOT** been saved. The SauceLabs job has ended, possibly timing out on their end.");
            return;
        } catch (Exception e) {
            getLogger().info("Screenshot of page '" + currentUrl + "' has **NOT** been saved. Will try again. Cause: " + e.getMessage());
            // Try it again.  WebDriver (on SauceLabs at least?) has blank-page and zero length files issues.
            try {
                savedIt = driverProvider.saveScreenshotTo(screenshotPath);
            } catch (Exception e1) {
                getLogger().error("Screenshot of page '" + currentUrl + "' has **NOT** been saved to '" + screenshotPath + "' because error '" + e.getMessage()
                        + "' encountered. Stack trace follows:\n" + getStringFromStackTrace(e));
                return;
            }
        }
        if (savedIt) {
            getLogger().info("Screenshot of page '" + currentUrl + "' has been saved to '" + screenshotPath + "' with " + new File(screenshotPath).length() + " bytes");
        } else {
            getLogger().error("Screenshot of page '" + currentUrl + "' has **NOT** been saved. If there is no error, perhaps the WebDriver type you are using is not compatible with taking screenshots");
        }
    }

    protected String screenshotPath(UUID uuid) {
        return String.format(screenshotPathPattern, reporterBuilder.outputDirectory(), uuid);
    }
}



