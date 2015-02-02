package com.griddynamics.qa.ui;

import org.jbehave.web.selenium.WebDriverProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Management class for page objects
 * In order to operate a page - add the page to this class
 */
public class Pages {
    /**
     * pointer to the current page
     */
    protected AbstractPage currentPage;

    @Autowired
    protected WebDriverProvider driverProvider;


    /**
     * window handle - unique identificator of a window
     */
    protected String baseWindowHandle;

    /**
     * pagesMap is a container where key is the page name of a Story, value is the page object
     */
    protected HashMap<String, AbstractPage> pagesMap = new HashMap<String, AbstractPage>();

    public Pages() {
    }

    public WebDriverProvider getDriverProvider() {
        return driverProvider;
    }

    public Pages(WebDriverProvider driverProvider) {
        this.driverProvider = driverProvider;
    }

    public String getBaseWindowHandle() {
        return baseWindowHandle;
    }

    public void setBaseWindowHandle() {
        this.baseWindowHandle = driverProvider.get().getWindowHandle();
    }

    public void setBaseWindowHandle(String handle) {
        this.baseWindowHandle = handle;
    }

    /**
     * @return pageName or empty string
     */
    public String getCurrentPageName() {
        for (String pageName : pagesMap.keySet()) {
            if (pagesMap.get(pageName).equals(currentPage)) {
                return pageName;
            }
        }
        return "";
    }

    public AbstractPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String pageStoryName) {
        this.currentPage = pagesMap.get(pageStoryName);
        assertThat("[ERROR] Invalid page name is provided: " + pageStoryName, currentPage, notNullValue());
    }

    public void setCurrentPage(AbstractPage currentPage) {
        this.currentPage = currentPage;
    }

}
