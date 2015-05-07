package com.griddynamics.web.pages;

import com.griddynamics.qa.ui.AbstractPage;
import com.griddynamics.web.GoogleHost;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;


import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.junit.Assert.assertTrue;

/**
 * @author lzakharova
 */

public abstract class CommonGooglePage extends AbstractPage {

    public static final String SEARCH_FIELD = "Search field";
    public static final By searchFieldLoc = By.xpath("//input[@type='text']");

    @Autowired
    public GoogleHost googleHost;


    private String currentPageTitle;
    private String currentPageUri;


    protected CommonGooglePage(WebDriverProvider driverProvider) {
        super(driverProvider);
        initElements();
    }

    protected CommonGooglePage(WebDriverProvider driverProvider, String currentPageUri, String currentPageTitle) {
        this(driverProvider);
        setCurrentPageUri(currentPageUri);
        setCurrentPageTitle(currentPageTitle);
        initElements();
    }

    private void initElements() {
        addElement(SEARCH_FIELD, searchFieldLoc);
    }

    public String getBaseURL() {
        return googleHost.getSiteUrl();
    }


    public String getCurrentPageTitle() {
        return currentPageTitle;
    }

    public void setCurrentPageTitle(String currentPageTitle) {
        this.currentPageTitle = currentPageTitle;
    }

    public String getCurrentPageUri() {
        return currentPageUri;
    }

    public void setCurrentPageUri(String currentPageUri) {
        this.currentPageUri = currentPageUri;
    }

    protected String getPageRelativeURL() {
        return currentPageUri;
    }

    @Override
    public String getPageURL() {
        return getBaseURL() + ("/" + getPageRelativeURL()).replaceAll("//", "/");
    }

    @Override
    public boolean checkCurrentPage() {
        return checkURL();
    }

    @Override
    public void assertCurrentPage() {
        assertTrue("AssertPage Failed due to wrong URL OR mandatory element absence.  " +
                "Expected URL: " + getPageURL() + "; Got: " + getCurrentUrl(),
                checkCurrentPage());
    }

    @Override
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
}
