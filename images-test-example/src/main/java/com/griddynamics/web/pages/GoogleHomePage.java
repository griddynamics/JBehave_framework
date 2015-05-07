package com.griddynamics.web.pages;

import org.jbehave.web.selenium.WebDriverProvider;

import static org.junit.Assert.assertTrue;

/**
 * @author lzakharova
 */
public class GoogleHomePage extends CommonGooglePage {

    public GoogleHomePage(WebDriverProvider driverProvider, String currentPageUri, String currentPageTitle) {
        super(driverProvider, currentPageUri, currentPageTitle);
    }

    @Override
    public void assertCurrentPage() {
        super.assertCurrentPage();
        assertTrue(SEARCH_FIELD + " is not displayed", isElementDisplayedOnPage(SEARCH_FIELD));
    }

}

