/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.framework;

import com.griddynamics.qa.logger.LoggerFactory;
import com.griddynamics.qa.ui.Pages;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.AfterStory;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.web.selenium.PerStoryWebDriverSteps;
import org.jbehave.web.selenium.WebDriverProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author tkmasr0/mlykosova
 */
public class CustomPerStoryWebDriverSteps extends PerStoryWebDriverSteps {
    @Autowired
    Pages pages;

    protected final static String WAIT_PAGE_OPEN_TIMEOUT_IN_MS = "page.timeout.ms";

    public static Logger logger = LoggerFactory.getLogger();

    public CustomPerStoryWebDriverSteps() {
        super(null);
    }

    public CustomPerStoryWebDriverSteps(WebDriverProvider driverProvider) {
        super(driverProvider);
    }

    @Override
    @BeforeStory
    /**
     * Action(s) before running each story:
     *  1. Initialize new instance of WebDriver;
     *  2. Clean browser cookies;
     *  3. Set page load timeout for most browsers, according to value of {@link com.griddynamics.qa.framework.CustomPerStoryWebDriverSteps#WAIT_PAGE_OPEN_TIMEOUT_IN_MS} property;
     *  4. Memorize window handle of the main window.
     */
    public void beforeStory() throws InterruptedException {
        if (!Boolean.getBoolean("doDryRun")) {
            driverProvider.initialize();
            getDriverProvider().get().manage().deleteAllCookies();

            if (!System.getProperty("browser").toLowerCase().matches("(chrome)|(ios.*)|(android.*)|(mobile.*)")) {
                getDriverProvider().get().manage().timeouts().pageLoadTimeout(Integer.valueOf(System.getProperty(WAIT_PAGE_OPEN_TIMEOUT_IN_MS)), TimeUnit.MILLISECONDS);
            }
            pages.setBaseWindowHandle();
        };
    }

    @Override
    @AfterStory
    /**
     * Action(s) after running each story:
     *  – End the current instance of WebDriver.
     */
    public void afterStory() throws Exception {
        if (!Boolean.getBoolean("doDryRun")) {
            super.afterStory();
        };

    }

}
