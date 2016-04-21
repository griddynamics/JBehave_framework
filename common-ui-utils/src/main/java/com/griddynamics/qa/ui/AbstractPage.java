/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.ui;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.TimeoutException;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author ybaturina
 * @author aluchin
 *         
 *         This is the abstract class which contains common page methods
 */
public abstract class AbstractPage extends CommonElementMethods {

    public final static int PAGE_OPEN_ATTEMPTS_NUMBER = 3;
    private final static int WAIT_WINDOW_OPEN_TIMEOUT_IN_MS = 10000;
    private final static int WAIT_PAGE_LOAD_TIMEOUT_IN_MS = 500;
    private final static int WAIT_FOR_PAGE_LOAD_TIMEOUT_IN_MS = 10000;
    private final static String SECURED_URL = "https";
    private final static String UNSECURED_URL = "http";

    protected String pageTitle;

    private boolean isSecuredURL;

    public AbstractPage(WebDriverProvider driverProvider) {
        super(driverProvider);
        setPageURLSecurity();
    }

    public boolean isSecuredURL() {
        return isSecuredURL;
    }

    public void setPageURLSecurity() {
        isSecuredURL = false;
    }

    public void setPageURLSecurity(boolean isSecuredURL) {
        this.isSecuredURL = isSecuredURL;
    }

    public abstract String getPageURL();


    public boolean isPageUrlSecured() {
        return getCurrentUrl().startsWith(SECURED_URL);
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public boolean checkURLAndTitle() {
        return checkURL() && checkTitle();
    }

    public void assertURLAndTitle() {
        assertURL();
    }

    public boolean checkURL() {
        return getCurrentUrl().startsWith(getPageURL());
    }

    public void assertURL() {
        String expectedProtocol = isSecuredURL() ? SECURED_URL : UNSECURED_URL;
        assertThat("[ERROR] This page is not secured though it should be.", getCurrentUrl(), startsWith(expectedProtocol));
        assertTrue("[ERROR] Expected URL [" + getPageURL() + "], was [" + getCurrentUrl() + "]",
                checkURL());
    }

    public boolean checkTitle() {
        return pageTitle.equals(getTitle());
    }

    public void assertTitle() {
        assertThat("[ERROR] Expected title is wrong. Expected title [" + pageTitle + "" + getTitle()
                + "], was [" + getTitle() + "].\n", getTitle(), is(pageTitle));
    }

    protected void open(int i) {
        getLogger().info("Opening page " + this.getClass().getSimpleName() + ": " + getPageURL() + ": " + i + " attempt");
        get(getPageURL());
        waitForPageToLoad();
    }

    public void openPage() {
        int timeoutExc = 0;
        int attempt = 0;
        while (timeoutExc < PAGE_OPEN_ATTEMPTS_NUMBER) {
            int i = 1;
            try {
                while (!checkCurrentPage() && i <= PAGE_OPEN_ATTEMPTS_NUMBER) {
                    attempt++;
                    open(attempt);
                    i++;
                }
            } catch (TimeoutException e) {
                timeoutExc++;
                getLogger().info("Caught TimeoutException. " + e.getMessage());
                continue;
            }
            timeoutExc = PAGE_OPEN_ATTEMPTS_NUMBER;
        }
        assertCurrentPage();
    }

    public void openURL(String url) {
        getLogger().info("Opening page " + url);
        get(url);
    }


    /**
     * Get Url and Title for new hadle and close it.
     *
     * @param baseWindowHandle baseWindowHandle
     * @param  handles handles
     * @return String[]. String[0] - New page title; String[1] - New page url;
     */
    private Map.Entry<String, String> getUrlAndTitleFromWindowHandleAndCloseIt(String baseWindowHandle, Set<String> handles) {
        Map.Entry<String, String> urlAndTitle = new AbstractMap.SimpleEntry<String, String>("", "");
        for (String handle : handles) {
            if (!handle.equals(baseWindowHandle)) {
                switchTo().window(handle);
                urlAndTitle = new AbstractMap.SimpleEntry<String, String>(getTitle(), getCurrentUrl());
                switchTo().window(handle).close();
                break;
            }
        }
        return urlAndTitle;
    }

    public Set<String> getNewWindowHandles(){
        Set<String> handles = getWindowHandles();
        assertThat("[ERROR] New window was not opened", getWindowHandles().size(), is(2));
        return handles;
    }

    /**
     * Check that new page is opened in new window
     * Then close new page
     *
     * @param baseWindowHandle previous page windowHandle
     */
    public void checkNewWindowOpened(String baseWindowHandle) {
        try {
            sleep(WAIT_WINDOW_OPEN_TIMEOUT_IN_MS);

            Set<String> handles = getNewWindowHandles();
            getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
            switchTo().window(baseWindowHandle);

        } catch (Exception e) {
            assertTrue("[ERROR] Unexpected exception happened while checking new window opened: " + e.getMessage(), false);
        }
    }

    /**
     * Check that new page is opened in new window. Assert Url and Title
     *
     * @param baseWindowHandle baseWindowHandle
     * @param pageURL pageURL
     * @param pageTitle pageTitle
     * @see #getUrlAndTitleFromWindowHandleAndCloseIt
     */
    public void checkNewWindowOpened(String baseWindowHandle, String pageURL, String pageTitle) {
        try {
            sleep(WAIT_WINDOW_OPEN_TIMEOUT_IN_MS);
            Set<String> handles = getNewWindowHandles();

            Map.Entry<String, String> urlAndTitle = getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
            switchTo().window(baseWindowHandle);

            assertThat(
                    "[ERROR] Invalid window was opened. Actual popup page title: "
                            + urlAndTitle.getKey() + ". Expected popup page title: "
                            + pageTitle, urlAndTitle.getKey(), is(pageTitle));
            assertThat(
                    "[ERROR] Invalid window was opened. Actual popup page URL: "
                            + urlAndTitle.getValue() + ". Expected popup page URL: "
                            + pageURL, urlAndTitle.getValue(), startsWith(pageURL));
        } catch (Exception e) {
            assertTrue("[ERROR] Unexpected exception happened while checking new window opened: " + e.getMessage(), false);
        }
    }

    /**
     * Check that new page is opened in new window. Assert Url
     *
     * @param baseWindowHandle baseWindowHandle
     * @see #getUrlAndTitleFromWindowHandleAndCloseIt
     */
    public void checkNewWindowOpenedUrlOnly(String baseWindowHandle) {

        sleep(WAIT_WINDOW_OPEN_TIMEOUT_IN_MS);
        Set<String> handles = getNewWindowHandles();
        Map.Entry<String, String> urlAndTitle = getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
        switchTo().window(baseWindowHandle);

        assertThat(
                "[ERROR] Invalid window was opened. Actual popup page URL: "
                        + urlAndTitle.getValue() + ". Expected popup page URL: "
                        + getPageURL(), urlAndTitle.getValue(), containsString(getPageURL()));
    }

    /**
     * Check that new page is opened in new window and Url contains text.
     *
     * @param baseWindowHandle baseWindowHandle
     * @param text text
     * @see #getUrlAndTitleFromWindowHandleAndCloseIt
     */
    public void checkNewWindowUrlContainsText(String baseWindowHandle, String text) {
        sleep(WAIT_WINDOW_OPEN_TIMEOUT_IN_MS);
        Set<String> handles = getNewWindowHandles();

        Map.Entry<String, String> urlAndTitle = getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
        switchTo().window(baseWindowHandle);

        assertThat(
                "[ERROR] URL of new window " + urlAndTitle.getValue() + " does not contain text: "
                        + text, urlAndTitle.getValue(), containsString(text));
    }

    public boolean checkCurrentPage() {
        return checkURLAndTitle() && checkBlocksDisplayed();
    }

    public void assertCurrentPage() {
        assertURLAndTitle();
        assertBlocksDisplayed();
    }

    /**
     * Wait for page is opened {@value #WAIT_FOR_PAGE_LOAD_TIMEOUT_IN_MS} ms
     */
    public void waitForPageToLoad() {
        long currentSleepCount = 0;
        long totalSleepCount = WAIT_FOR_PAGE_LOAD_TIMEOUT_IN_MS / WAIT_PAGE_LOAD_TIMEOUT_IN_MS;
        while (!checkCurrentPage() && currentSleepCount < totalSleepCount) {
            currentSleepCount++;
            sleep(WAIT_PAGE_LOAD_TIMEOUT_IN_MS);
        }
    }

    /**
     * Wait for page is opened {@value #WAIT_FOR_PAGE_LOAD_TIMEOUT_IN_MS} ms
     * @param oldURL oldURL
     */
    public void waitForPageToLoad(String oldURL) {
        String newURL = getDriver().getCurrentUrl();

        long currentSleepCount = 0;
        long totalSleepCount = WAIT_FOR_PAGE_LOAD_TIMEOUT_IN_MS / WAIT_PAGE_LOAD_TIMEOUT_IN_MS;
        while (newURL.equals(oldURL) && currentSleepCount < totalSleepCount) {
            currentSleepCount++;
            sleep(WAIT_PAGE_LOAD_TIMEOUT_IN_MS);
            newURL = getDriver().getCurrentUrl();
        }
        assertThat("[ERROR] New page is not opened", newURL, not(oldURL));
    }

    public boolean isBlockLoaded(ElementBlock elementBlock) {
        return isHiddenElementLoaded(elementBlock.getLocator());
    }

    /**
     * Check that all blocks are present on the page
     *
     * @return true in case when all blocks displayed or there is no one blocks on the page.
     */

    public boolean checkBlocksDisplayed() {
        boolean result = true;

        for (ElementBlock block : getBlockList()) {
            result = result && block.isBlockDisplayed();
        }
        return result;
    }

    /**
     * Check that all blocks are present on the page
     *
     * return
     */

    public void assertBlocksDisplayed() {
        for (ElementBlock block : getBlockList()) {
            assertTrue("[ERROR] Block \"" + block.getName() + "\" with locator: "
                    + block.getLocator() + " is not displayed on the page", block.isBlockDisplayed());
        }
    }

    /**
     * Method is used to Switch the focus to Base Window
     * @param baseWindowHandle baseWindowHandle
     */
    public void switchToBaseWindow(String baseWindowHandle) {
        Set<String> handles = getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(baseWindowHandle)) {
                switchTo().window(handle).close();
            }
        }
        switchTo().window(baseWindowHandle);
    }

    /**
     * Method is to Switch the focus to New Window
     * @param baseWindowHandle baseWindowHandle
     */
    public void switchToNewWindow(String baseWindowHandle) {
        Set<String> handles = getNewWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(baseWindowHandle)) {
                switchTo().window(handle);
            }
        }
    }

    /**
     * Method is to check new window is opened
     * @return boolean
     */
    public boolean isNewWindowOpened() {
        return (getWindowHandles().size() == 1) ? false : true;
    }

    /**
     * Method is to close base window and to switch to new window
     * @param baseWindowHandle baseWindowHandle
     */

    public void closeBaseWindow(String baseWindowHandle) {
        Set<String> handles = getNewWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(baseWindowHandle)) {
                switchTo().window(handle);
                switchTo().window(handle).close();
                break;
            }
        }
        switchTo().window(baseWindowHandle);
    }

    /**
     * This method should be overriden in child classes in case it is necessary
     * to initialize dynamic web elements om the page
     */
    public void gatherElements() {
    }

    /**
     * @param time Sleep time in millisecond
     */
    public void sleep(long time) {
        try {
            getLogger().info("Sleep for " + time + " milliseconds...");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            assertTrue("[ERROR] Interrupted exception was thrown during timeout: " + e.getMessage(), false);
        }
    }


}
