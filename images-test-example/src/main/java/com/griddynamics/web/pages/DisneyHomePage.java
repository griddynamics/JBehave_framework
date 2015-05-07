package com.griddynamics.web.pages;

import org.jbehave.web.selenium.WebDriverProvider;

import static org.junit.Assert.assertTrue;

/**
 * @author lzakharova
 */
public class DisneyHomePage extends CommonDisneyPage {

    public DisneyHomePage(WebDriverProvider driverProvider, String currentPageUri, String currentPageTitle) {
        super(driverProvider, currentPageUri, currentPageTitle);
    }

    @Override
    public void assertCurrentPage() {
        super.assertCurrentPage();
//        assertTrue(DISNEY_LOGO + " is not displayed", isElementDisplayedOnPage(DISNEY_LOGO));
    }
}

