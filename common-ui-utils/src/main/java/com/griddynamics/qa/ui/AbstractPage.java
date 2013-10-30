package com.griddynamics.qa.ui;

import org.jbehave.web.selenium.WebDriverProvider;
import org.junit.Assert;

import java.util.Set;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * @author ybaturina
 * @author aluchin
 *         <p/>
 *         This is the abstract class which contains common page methods
 */
public abstract class AbstractPage extends CommonElementMethods {

    public final static int PAGE_OPEN_ATTEMPTS_NUMBER = 3;
    private final static int WAIT_WINDOW_OPEN_TIMEOUT_IN_MS = 10000;
    private final static int WAIT_PAGE_LOAD_TIMEOUT_IN_MS = 500;
    private final static int WAIT_FOR_PAGE_LOAD_TIMEOUT_IN_MS = 10000;

    protected String pageURL;
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

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public void setAbsolutePageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public boolean isPageUrlSecured() {
        return getCurrentUrl().startsWith("https");
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
        return getCurrentUrl().startsWith(pageURL);
    }

    public void assertURL() {
        String expectedProtocol = isSecuredURL() ? "https" : "http";
        assertTrue("[ERROR] This page is not secured though it should be.", getCurrentUrl().startsWith(expectedProtocol));
        assertTrue("Expected URL [" + pageURL + "], was [" + getCurrentUrl() + "]",
                checkURL());
    }

    public boolean checkTitle() {
        return pageTitle.equals(getTitle());
    }

    public void assertTitle() {
        assertEquals("Expected title is wrong. Expected title [" + pageTitle + "" + getTitle()
                + "], was [" + getTitle() + "].\n", pageTitle, getTitle());
    }

    protected void open(int i) {
        getLogger().info("Opening page " + this.getClass().getSimpleName() + ": " + pageURL + ": " + i + " attempt");
        get(pageURL);
        waitForPageToLoad();
    }

    public void openPage() {
        if (!checkCurrentPage()) {
            for (int i = 1; i <= PAGE_OPEN_ATTEMPTS_NUMBER; i++) {
                open(i);
                if (checkCurrentPage()) {
                    break;
                }
            }
            assertCurrentPage();
        }
    }

    public void openURL(String url) {
        getLogger().info("Opening page " + url);
        get(url);
    }


    /**
     * Get Url and Title for new hadle and close it.
     *
     * @param baseWindowHandle
     * @param handles
     * @return String[]. String[0] - New page title; String[1] - New page url;
     */
    private String[] getUrlAndTitleFromWindowHandleAndCloseIt(String baseWindowHandle, Set<String> handles) {

        String urlAndTitle[] = new String[2];
        urlAndTitle[0] = "";
        urlAndTitle[1] = "";

        for (String handle : handles) {
            if (!handle.equals(baseWindowHandle)) {
                switchTo().window(handle);
                urlAndTitle[0] = getTitle();
                urlAndTitle[1] = getCurrentUrl();
                switchTo().window(handle).close();
                break;
            }
        }

        return urlAndTitle;
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

            Set<String> handles = getWindowHandles();

            assertTrue("[Error] New window was not opened", getWindowHandles().size() == 2);
            getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
            switchTo().window(baseWindowHandle);

        } catch (Exception e) {
            assertTrue("Unexpected exception happened while checking new window opened: " + e.getMessage(), false);
        }
    }

    /**
     * Check that new page is opened in new window. Assert Url and Title
     *
     * @param baseWindowHandle
     * @param pageURL
     * @param pageTitle
     * @see #getUrlAndTitleFromWindowHandleAndCloseIt
     */
    public void checkNewWindowOpened(String baseWindowHandle, String pageURL, String pageTitle) {
        try {
            sleep(WAIT_WINDOW_OPEN_TIMEOUT_IN_MS);
            Set<String> handles = getWindowHandles();
            assertTrue("[Error] New window was not opened", getWindowHandles().size() == 2);

            String[] urlAndTitle = getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
            switchTo().window(baseWindowHandle);

            assertEquals(
                    "[Error] Invalid window was opened. Actual popup page title: "
                            + urlAndTitle[0] + ". Expected popup page title: "
                            + pageTitle, pageTitle, urlAndTitle[0]);
            assertTrue(
                    "[Error] Invalid window was opened. Actual popup page URL: "
                            + urlAndTitle[1] + ". Expected popup page URL: "
                            + pageURL, urlAndTitle[1].startsWith(pageURL));
        } catch (Exception e) {
            assertTrue("Unexpected exception happened while checking new window opened: " + e.getMessage(), false);
        }
    }

    /**
     * Check that new page is opened in new window. Assert Url
     *
     * @param baseWindowHandle
     * @see #getUrlAndTitleFromWindowHandleAndCloseIt
     */
    public void checkNewWindowOpenedUrlOnly(String baseWindowHandle) {

        sleep(WAIT_WINDOW_OPEN_TIMEOUT_IN_MS);
        Set<String> handles = getWindowHandles();
        assertTrue("[Error]: New window was not opened.",
                getWindowHandles().size() == 2);
        String[] urlAndTitle = getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
        switchTo().window(baseWindowHandle);

        assertTrue(
                "[Error]: Invalid window was opened. Actual popup page URL: "
                        + urlAndTitle[1] + ". Expected popup page URL: "
                        + getPageURL(), urlAndTitle[1].contains(getPageURL()));

    }

    /**
     * Check that new page is opened in new window and Url contains text.
     *
     * @param baseWindowHandle
     * @param text
     * @see #getUrlAndTitleFromWindowHandleAndCloseIt
     */
    public void checkNewWindowUrlContainsText(String baseWindowHandle, String text) {
        sleep(WAIT_WINDOW_OPEN_TIMEOUT_IN_MS);
        Set<String> handles = getWindowHandles();

        assertTrue("[Error]: New window was not opened.",
                getWindowHandles().size() == 2);

        String[] urlAndTitle = getUrlAndTitleFromWindowHandleAndCloseIt(baseWindowHandle, handles);
        switchTo().window(baseWindowHandle);

        assertTrue(
                "[Error]: URL of new window " + urlAndTitle[1] + " does not contain text: "
                        + text, urlAndTitle[1].contains(text));
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
     */
    public void waitForPageToLoad(String oldURL) {
        String newURL = getDriverProvider().get().getCurrentUrl();

        long currentSleepCount = 0;
        long totalSleepCount = WAIT_FOR_PAGE_LOAD_TIMEOUT_IN_MS / WAIT_PAGE_LOAD_TIMEOUT_IN_MS;
        while (newURL.equals(oldURL) && currentSleepCount < totalSleepCount) {
            currentSleepCount++;
            sleep(WAIT_PAGE_LOAD_TIMEOUT_IN_MS);
            newURL = getDriverProvider().get().getCurrentUrl();
        }
        assertThat("New page is not opened", newURL, not(equalTo(oldURL)));
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
     * @return
     */

    public void assertBlocksDisplayed() {
        for (ElementBlock block : getBlockList()) {
            assertTrue("Block \"" + block.getName() + "\" with locator: "
                    + block.getLocator() + " is not displayed on the page", block.isBlockDisplayed());
        }
    }

    /**
     * Method is used to Switch the focus to Base Window
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
     */
    public void switchToNewWindow(String baseWindowHandle) {
        Set<String> handles = getWindowHandles();
        Assert.assertTrue("[Error] New window was not opened",
                getWindowHandles().size() == 2);
        for (String handle : handles) {
            if (!handle.equals(baseWindowHandle)) {
                switchTo().window(handle);
            }
        }
    }

    /**
     * Method is to check new window is opened
     */
    public boolean isNewWindowOpened() {
        boolean newWindowOpened = true;
        if (getWindowHandles().size() == 1) {
            newWindowOpened = false;
        }
        return newWindowOpened;
    }

    /**
     * Method is to close base window and to switch to new window
     */

    public void closeBaseWindow(String baseWindowHandle) {
        Set<String> handles = getWindowHandles();
        Assert.assertTrue("[Error] New window was not opened",
                getWindowHandles().size() == 2);
        for (String handle : handles) {
            if (!handle.equals(baseWindowHandle)) {
                switchTo().window(handle);

                switchTo().window(handle).close();
                break;
            }
        }

        switchTo().window(baseWindowHandle);
    }

    public void gatherElements() {
    }

    /**
     * @param time Sleep time in millisecond
     */
    public void sleep(int time) {
        try {
            getLogger().info("Sleep for " + time + " milliseconds...");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            assertTrue("Interrupted exception was thrown during timeout: " + e.getMessage(), false);

        }
    }


}
