/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.ui.steps;

import com.griddynamics.qa.tools.rest.TestRequest;
import com.griddynamics.qa.ui.ElementBlock;
import com.griddynamics.qa.ui.Pages;
import com.griddynamics.qa.ui.utils.TimeoutConstants;
import org.jbehave.core.annotations.*;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;


/**
 * @author ybaturina
 * @author mlykosova
 * @author aluchin
 *         Entity class containing common steps implementation
 */

@Component
@Scope("thread")
public class CommonPageSteps implements TimeoutConstants{

    @Autowired
    private Pages pages;

    public CommonPageSteps() { // constructor
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }

    @Given("{running|Running} given stories")
    public void givenRunGivenStories() {
    }

    /**
     * Search every block on the current page for Web element then click it
     * priority=0
     *
     * @param elementName HTML element name from a Story
     */
    @When("customer clicks $elementName")
    public void click(String elementName) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, true);
        pages.getCurrentPage().waitForLocatorClickable(loc, DEFAULT_TIMEOUT_IN_SECONDS);
        try {
            pages.getCurrentPage().click(elementName);
        } catch (TimeoutException e) {
            getLogger().warn(e.getMessage());
        } catch (StaleElementReferenceException e){
            pages.getCurrentPage().waitForLocatorClickable(loc, DEFAULT_TIMEOUT_IN_SECONDS);
            pages.getCurrentPage().click(elementName);
        }
    }


    @When(value = "customer clicks on $elementName", priority = 1)
    public void clickOn(String elementName) {
        click(elementName);
    }

    /**
     * Check that attribute contains attribute value for displayed element
     *
     *@param attrName attrName
     *@param  elementName elementName
     *@param attrValue attrValue
     */
    @Then("attribute $attrName of element $elementName contains $attrValue")
    public void thenElementContainsStringInAttribute(String attrName,
                                                     String elementName,
                                                     String attrValue) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, true);
        pages.getCurrentPage().waitForLocatorAttributeValue(loc, attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
        String currentAttrValue = "";
        try {
            currentAttrValue = pages.getCurrentPage().getElementAttributeValue(elementName, attrName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForLocatorAttributeValue(loc, attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
            currentAttrValue = pages.getCurrentPage().getElementAttributeValue(elementName, attrName);
        }
        assertThat("[ERROR] Element " + elementName + " has attribute " + attrName + " with value "
                + currentAttrValue, currentAttrValue, containsString(attrValue));
    }

    /**
     * Check that displayed element doesn't contain attribute
     *
     *@param elementName elementName
     *@param attribute attribute
     */
    @Then("element $elementName doesn't contain attribute $attribute")
    public void thenElementDoesNotContainAttribute(String elementName,
                                                   String attribute) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, true);
        pages.getCurrentPage().waitForLocatorAttributeNoValue(loc, attribute, "", DEFAULT_TIMEOUT_IN_SECONDS);
        String currentAttrValue = "";
        try {
            currentAttrValue = pages.getCurrentPage().getElementAttributeValue(elementName, attribute);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForLocatorAttributeNoValue(loc, attribute, "", DEFAULT_TIMEOUT_IN_SECONDS);
            currentAttrValue = pages.getCurrentPage().getElementAttributeValue(elementName, attribute);
        }
        assertNull("[ERROR] Element " + elementName + "should not contain attribute " +
                attribute + ", but it does", currentAttrValue);
    }

    /**
     * Check that attribute does not contain attribute value for displayed element
     *
     *@param attrName attrName
     *@param elementName elementName
     *@param attrValue attrValue
     */
    @Then("attribute $attrName of element $elemName does not contain $attrValue")
    public void thenElementDoesNotContainStringInAttribute(String attrName,
                                                           String elementName,
                                                           String attrValue) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, true);
        pages.getCurrentPage().waitForLocatorAttributeNoValue(loc, attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
        String currentAttrValue = "";
        try {
            currentAttrValue = pages.getCurrentPage().getElementAttributeValue(elementName, attrName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForLocatorAttributeNoValue(loc, attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
            currentAttrValue = pages.getCurrentPage().getElementAttributeValue(elementName, attrName);
        }
        assertThat("[ERROR] Element " + elementName + " has attribute " + attrName + " with value "
                + currentAttrValue, currentAttrValue, not(containsString(attrValue)));
    }


    /**
     * Check that block present on page and it locator does not contain attribute with value from story
     *
     *@param attrName attrName
     *@param blockName blockName
     *@param attrValue attrValue
     * @throws RuntimeException if block was not initialized for page
     */
    @Then("attribute $attrName of block $blockName locator does not contain $attrValue")
    public void thenBlockDoesNotContainStringInAttribute(String attrName,
                                                         String blockName,
                                                         String attrValue) {
        ElementBlock block = pages.getCurrentPage().getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("[ERROR] Block with name " + blockName
                    + " was not found in page class");
        }
        pages.getCurrentPage().waitForLocatorAttributeNoValue(block.getLocator(), attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
        String currentAttrValue = "";
        try {
            currentAttrValue = pages.getCurrentPage().getLocatorAttributeValue(block.getLocator(), attrName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForLocatorAttributeNoValue(block.getLocator(), attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
            currentAttrValue = pages.getCurrentPage().getLocatorAttributeValue(block.getLocator(), attrName);
        }
        assertThat("[ERROR] Block " + blockName + " locator has attribute " + attrName + " with value "
                + currentAttrValue, currentAttrValue, not(containsString(attrValue)));
    }


    /**
     * Check that block present on page and it locator contains attribute with value from story
     *
     *@param attrName attrName
     *@param blockName blockName
     *@param attrValue attrValue
     * @throws RuntimeException if block was not initialized for page
     */
    @Then("attribute $attrName of block $blockName locator contains $attrValue")
    public void thenBlockContainsStringInAttribute(String attrName,
                                                   String blockName,
                                                   String attrValue) {
        ElementBlock block = pages.getCurrentPage().getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("[ERROR] Block with name " + blockName
                    + " was not found in page class");
        }
        pages.getCurrentPage().waitForLocatorAttributeValue(block.getLocator(), attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
        String currentAttrValue = "";
        try {
            currentAttrValue = pages.getCurrentPage().getLocatorAttributeValue(block.getLocator(), attrName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForLocatorAttributeValue(block.getLocator(), attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
            currentAttrValue = pages.getCurrentPage().getLocatorAttributeValue(block.getLocator(), attrName);
        }
        assertThat("[ERROR] Block " + blockName + " locator has attribute " + attrName + " with value "
                + currentAttrValue, currentAttrValue, containsString(attrValue));
    }

    /**
     * Check that list of links from story is displayed on page
     *
     * @param elements link enumeration with separator ","
     */
    @Then("the links $links are displayed on page")
    public void isLinksDisplayed(String elements) {
        String[] splits = elements.split("\\s*,\\s*");
        for (String element : splits) {
            assertTrue("[ERROR] Element " + element + " is not displayed",
                    pages.getCurrentPage().isElementDisplayedOnPage(element));
        }
    }

    /**
     * Find HTML element in the single block on the current page then click on it
     *
     * @param elementName HTML element name from a Story
     * @param blockName  blockName
     */
    @When(value = "customer clicks on $elementName in the $blockName", priority = 2)
    public void clickOnInTheBlock(String elementName, String blockName) {
        pages.getCurrentPage().getBlockByName(blockName).waitForElementClickable(elementName, DEFAULT_TIMEOUT_IN_SECONDS);
        try {
            pages.getCurrentPage().click(pages.getCurrentPage().findElementInBlock(blockName, elementName));
        } catch (TimeoutException e) {
            getLogger().warn(e.getMessage());
        } catch (StaleElementReferenceException e1) {
            pages.getCurrentPage().getBlockByName(blockName).waitForElementClickable(elementName, DEFAULT_TIMEOUT_IN_SECONDS);
            pages.getCurrentPage().click(pages.getCurrentPage().findElementInBlock(blockName, elementName));
        }
    }

    /**
     * Find HTML element in the single block on the current page then type in it
     *
     * @param elementName HTML element name from a Story
     * @param blockName blockName
     * @param text text
     */
    @When(value = "customer types $text in $elementName in the $blockName", priority = 2)
    public void typeInElementInTheBlock(String text, String elementName, String blockName) {
        pages.getCurrentPage().typeTextInBlock(blockName, elementName,text);
    }

    /**
     * Find HTML element in the single block on the current page then select element in it
     *
     * @param elementName HTML element name from a Story
     * @param blockName blockName
     * @param text text
     */
    @When(value = "customer selects $text in $elementName in the $blockName", priority = 3)
    public void selectInElementInTheBlock(String text, String elementName, String blockName) {
        pages.getCurrentPage().selectTextInBlock(blockName, elementName,text);
    }

    /**
     * Clear field of elementName and type text
     *
     *@param text text
     *@param elementName elementName
     */
    @When("customer types $text in $elementName")
    @Alias("customer writes $text at $elementName")
    public void whenTypeText(String text, String elementName) {
        pages.getCurrentPage().isElementLoaded(elementName);
        try {
            pages.getCurrentPage().typeText(elementName, text);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().isElementLoaded(elementName);
            pages.getCurrentPage().typeText(elementName, text);
        }
    }

    /**
     * Clear field elementName
     *
     *@param elementName elementName
     */
    @When("customer clears $elementName")
    public void whenClearField(String elementName) {
        pages.getCurrentPage().isElementLoaded(elementName);
        try {
            pages.getCurrentPage().clearField(elementName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().isElementLoaded(elementName);
            pages.getCurrentPage().clearField(elementName);
        }
    }

    /**
     * Click or submit elementName
     *
     *@param elementName elementName
     */
    @When("customer submits $elementName")
    public void submit(String elementName) {
        pages.getCurrentPage().isElementLoaded(elementName);
        try {
            pages.getCurrentPage().submit(elementName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().isElementLoaded(elementName);
            pages.getCurrentPage().submit(elementName);
        }
    }

    @When(value = "customer submits button $elementName", priority = 2)
    public void submitButton(String elementName) {
        submit(elementName);
    }


    @When(value = "customer selects $text in $elementName", priority = 2)
    public void selectText(String text, String elementName) {
        pages.getCurrentPage().isElementLoaded(elementName);
        try {
            pages.getCurrentPage().selectText(elementName, text);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().isElementLoaded(elementName);
            pages.getCurrentPage().selectText(elementName, text);
        }
    }

    /**
     * Set checkbox on true or false
     *
     * @param value       boolean
     * @param elementName from story
     */
    @When("customer sets $value in checkbox $elementName")
    @Alias("customer sets $value in $elementName checkbox")
    public void setCheckbox(Boolean value, String elementName) {
        pages.getCurrentPage().isElementLoaded(elementName);
        try {
            pages.getCurrentPage().setCheckbox(elementName, value);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().isElementLoaded(elementName);
            pages.getCurrentPage().setCheckbox(elementName, value);
        }
    }

    /**
     * Set Page this $pageName as current
     * Wait while page is load {@link com.griddynamics.qa.ui.AbstractPage#waitForPageToLoad}
     * Then assert it
     *
     * @param pageName page name from a Story
     */
    @Then("$pageName Page is opened")
    public void thenPageIsOpened(String pageName) {
        pages.setCurrentPage(pageName);
        pages.getCurrentPage().waitForPageToLoad();
        pages.getCurrentPage().assertCurrentPage();
    }

    /**
     * Check that page Url for pageName starts with https
     *
     *@param pageName pageName
     */
    @Then("$pageName Page has secured url")
    public void thenPageUrlSecured(String pageName) {
        assertTrue("[ERROR] Page " + pageName + " has not secured url " + pages.getCurrentPage().getPageURL(),
                pages.getCurrentPage().isPageUrlSecured());
    }

    /**
     * Open the page with pageName, check if the page is opened then assert it
     * Current page is set into this method
     *
     * @param pageName page name from a Story
     */
    @Given("anonymous customer is on $pageName Page")
    @Alias("customer is on $pageName Page")
    public void whenCustomerOpensPage(String pageName) {
        pages.setCurrentPage(pageName);
        pages.getCurrentPage().openPage();
    }


    /**
     * Call refresh for current page
     */
    @When("current page is refreshed")
    public void currentPageIsRefreshed() {
        assertNotNull("[ERROR] Current Page is not set", pages.getCurrentPage());
        pages.getCurrentPage().navigate().refresh();             //  can be some problems in IE8
    }


    @Then(value = "checkbox $elementName has value $value", priority = 1)
    public void thenCheckboxHasValue(String elementName, Boolean val) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, true);
        pages.getCurrentPage().waitForLocatorVisibility(loc, DEFAULT_TIMEOUT_IN_SECONDS);
        try {
            assertThat("[ERROR] Checkbox " + elementName + " has another value: ",
                    pages.getCurrentPage().isChecked(elementName), is(val));
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForLocatorVisibility(loc, DEFAULT_TIMEOUT_IN_SECONDS);
            assertThat("[ERROR] Checkbox " + elementName + " has another value: ",
                    pages.getCurrentPage().isChecked(elementName), is(val));
        }
    }


    @Then("element $elementName has text $text")
    @Aliases(values = {"\"$text\" $elementName is displayed"})
    public void thenElementHasText(@Named("elementName") String elementName, @Named("text") String text) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, true);
        pages.getCurrentPage().waitForElementContainsText(elementName, text, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    @Then("element $elementName has value $value")
    public void thenElementHasValue(String elementName, String value) {
        pages.getCurrentPage().waitForElementAttributeValue(elementName, "value", value, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    /**
     * @param blockName from a Story
     * @param value     from a Story. Possible values: "present", "not present", "displayed", "not displayed"
     */
    @Then("block $blockName is $value")
    public void thenBlockIsDisplayed(String blockName, String value) {
        assertThat("[ERROR] Invalid name of step: should be 'block " + blockName + " is present', 'block " + blockName
                + " is displayed', 'block " + blockName + " is not present' or 'block "
                + blockName + " is not displayed'", value, isOneOf("present", "not present", "displayed", "not displayed"));

        ElementBlock block = pages.getCurrentPage().getBlockByName(blockName);
        if (block == null) {
            if (value.equals("present") || value.equals("displayed")) {
                throw new RuntimeException("[ERROR] Block with name " + blockName
                        + " was not found in page class");
            }
        } else {
            if (value.equals("displayed")) {
                assertTrue("[ERROR] Block " + blockName + " is not displayed though it should be",
                        block.isBlockDisplayed());
            } else if (value.equals("not displayed")) {
                assertTrue("[ERROR] Block " + blockName + " is displayed though it shouldn't be",
                        block.isBlockNotDisplayed());
            } else if (value.equals("not present")) {
                assertTrue("[ERROR] Block " + blockName + " is present though it shouldn't be",
                        !pages.getCurrentPage().isLocatorPresentOnPage(block.getLocator()));
            } else if (value.equals("present")) {
                assertTrue("[ERROR] Block " + blockName + " is not present though it should be",
                        pages.getCurrentPage().isLocatorPresentOnPage(block.getLocator()));
            }
        }
    }

    /**
     * Check blockName contains quantity blocks
     *
     * @param blockName from a Story
     * @param quantity  from a Story
     */
    @Then("block $blockName has $quantity blocks")
    public void thenBlockHasBlocks(String blockName, int quantity) {
        ElementBlock block = pages.getCurrentPage().getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("[ERROR] Block with name " + blockName
                    + " was not found in page class");
        }
        assertThat("[ERROR] Block " + blockName + " has " + block.getBlockList().size() + " blocks instead of " + quantity,
                block.getBlockList().size(), is(quantity));
    }

    /**
     * Check blockName contains quantity elements
     *
     *@param name name
     *@param quantity quantity
     */
    @Then("block $name has $quantity elements")
    public void thenBlockHasElements(String name, int quantity) {
        ElementBlock block = pages.getCurrentPage().getBlockByName(name);
        if (block == null) {
            throw new RuntimeException("[ERROR] Block with name " + name
                    + " was not found in page class");
        }
        assertThat("[ERROR] Block " + name + " has " + block.getElementsMap().size() + " elements instead of " + quantity,
                block.getElementsMap().size(), is(quantity));
    }


    /**
     * Check that elements in block ordered or not ordered  in alphabetical
     *
     *@param blockName blockName
     * @param order     Possible values: "ordered", "not ordered"
     * @param elements  list of elements divided by comma sign
     */
    @Then("block $blockName has $order elements $elements")
    public void thenBlockHasOrderedElementsList(String blockName,
                                                String order,
                                                ArrayList<String> elements) {
        assertThat("[ERROR] Invalid name of step: should be 'ordered' or 'not ordered'", order, isOneOf("ordered", "not ordered"));
        ElementBlock block = pages.getCurrentPage().getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("[ERROR] Block with name " + blockName
                    + " was not found in page class");
        }
        if (order.equals("ordered")) {
            List<String> expNames = new ArrayList<String>(elements);
            List<String> actNames = new ArrayList<String>(block.getElementsMap().keySet());
            assertThat("[ERROR] Expected elements: " + expNames.toString() + "; actual elements " + actNames.toString(), actNames, is(expNames));
        } else {
            Set<String> expNames = new HashSet<String>(elements);
            Set<String> actNames = block.getElementsMap().keySet();
            assertThat("[ERROR] Expected elements: " + expNames.toString() + "; actual elements " + actNames.toString(), actNames, is(expNames));
        }
    }

    /**
     * Check that blocks in block ordered or not ordered  in alphabetical
     *
     *@param blockName blockName
     * @param order     Possible values: "ordered", "not ordered"
     * @param expNames    list of blocks divided by comma sign
     */
    @Then("block $blockName has $order blocks $blocks")
    public void thenBlockHasOrderedBlocksList(String blockName,
                                              String order,
                                              ArrayList<String> expNames) {
        assertThat("[ERROR] Invalid name of step: should be 'ordered' or 'not ordered'", order, isOneOf("ordered", "not ordered"));
        ElementBlock block = pages.getCurrentPage().getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("[ERROR] Block with name " + blockName
                    + " was not found in page class");
        }
        List<String> actNames = new ArrayList<String>();
        List<ElementBlock> actBlocks = block.getBlockList();
        for (ElementBlock bl : actBlocks) {
            actNames.add(bl.getName());
        }
        if (order.equals("ordered")) {
            assertEquals("[ERROR] Expected blocks: " + expNames.toString() + "; actual blocks " + actNames.toString(), actNames, expNames);
        } else {
            assertTrue("[ERROR] Expected blocks: " + expNames.toString() + "; actual blocks " + actNames.toString(),
                    actNames.containsAll(expNames) && expNames.containsAll(actNames));
        }

    }

    /**
     * Check state of element according to val
     *
     *@param elementName elementName
     * @param val         Possible values: "present", "not present", "displayed", "not displayed"
     */
    @Then("$elementName is $value on page")
    public void thenElementIsPresent(String elementName, String val) {
        assertThat("[ERROR] Invalid name of step: should be '" + elementName + " is present on page', '" + elementName
                + " is displayed on page', '" + elementName + " is not displayed on page' or '"
                + elementName + " is not present on page'", val, isOneOf("present", "not present", "displayed", "not displayed"));

        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, false, false);
        assertNotNull("[ERROR] Element " + elementName + " has nullable locator", loc);

        if (val.equals("present")) {
            assertTrue("[ERROR] Element " + elementName + " with locator " + loc + " is not present on page",
                    pages.getCurrentPage().isLocatorPresentOnPage(loc));
        } else if (val.equals("not present")) {
            assertFalse("[ERROR] Element " + elementName + " with locator " + loc + " is present on page",
                    pages.getCurrentPage().isLocatorPresentOnPage(loc));
        } else if (val.equals("displayed")) {
            assertTrue("[ERROR] Element " + elementName + " with locator " + loc + " is not displayed",
                    pages.getCurrentPage().isLocatorDisplayedOnPage(loc));
        } else if (val.equals("not displayed")) {
            assertFalse("[ERROR] Element " + elementName + " with locator " + loc + " is displayed",
                    pages.getCurrentPage().isLocatorDisplayedOnPage(loc, false, DEFAULT_TIMEOUT_IN_SECONDS));
        }
    }

    /**
     * @param time sleep time in seconds
     */
    @Given("customer does nothing $n second{s|}")
    @When("customer does nothing $n second{s|}")
    @Then("customer does nothing $n second{s|}")
    public void customerDoNothing(@Named("$n") Integer time) {
        /*
        customer does nothing n+1 seconds, n - session timeout.
		convert seconds to milliseconds
		*/
        pages.getCurrentPage().sleep(time * 1000);
    }

    /**
     * check response code is equal to expectedCode
     *
     *@param url url
     *@param expectedCode expectedCode
     * @throws Exception if catch
     */
    @Then("check response code for $url equal to $expectedCode")
    public void anotherCheckResponseCodeForUrl(String url, int expectedCode) throws Exception {
        try {
            TestRequest request = new TestRequest(url);
            request.get();
            int code = request.getResponse().getStatusCode();
            getLogger().info("Status code is: " + code);
            assertThat("[ERROR] Response code for url " + url + " is " + "code", code, is(expectedCode));
        } catch (Exception e) {
            throw new RuntimeException("<<< WARNING! Connection to host refused! >>> ", e);
        }
    }

    /**
     * WebDriver has a known issue: sometimes method click() causes tests to hang.
     * As a workaround, the method uses javascript to click the element.
     *
     *@param elementName elementName
     */
    @When(value = "customer clicks $elementName with javascript", priority = 2)
    public void whenClickWithJs(String elementName) {
        try {
            pages.getCurrentPage().clickElementWithScript(elementName);
        } catch (TimeoutException e) {
            getLogger().warn(e.getMessage());
        } catch (StaleElementReferenceException e){
            pages.getCurrentPage().waitUntilNotStaleElement(pages.getCurrentPage().getElementLocatorByName(elementName, true, false),
                    DEFAULT_TIMEOUT_IN_SECONDS);
            pages.getCurrentPage().clickElementWithScript(elementName);
        }
    }

    /**
     * If element is partially placed under other element, method element.click() may doesn't work.
     * As a workaround the method uses Actions to click the element.
     *
     *@param elementName elementName
     */
    @When(value = "customer clicks $elementName with actions", priority = 2)
    public void whenClickWithActions(String elementName) {
        try {
            new Actions(pages.getCurrentPage().getDriver()).moveToElement(pages.getCurrentPage().getElementByName(elementName)).click().perform();
        } catch (TimeoutException e) {
            getLogger().warn(e.getMessage());
        } catch (StaleElementReferenceException e){
            pages.getCurrentPage().waitUntilNotStaleElement(pages.getCurrentPage().getElementLocatorByName(elementName, true, false),
                    DEFAULT_TIMEOUT_IN_SECONDS);
            new Actions(pages.getCurrentPage().getDriver()).moveToElement(pages.getCurrentPage().getElementByName(elementName)).click().perform();
        }
    }


    @Then("element $elementName does not contain $text")
    public void elementNotContainsText(String elementName, String text) {
        pages.getCurrentPage().waitForElementContainsNoText(elementName, text, DEFAULT_TIMEOUT_IN_SECONDS);
        String currText = "";
        try {
            currText = pages.getCurrentPage().getElementText(elementName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForElementContainsNoText(elementName, text, DEFAULT_TIMEOUT_IN_SECONDS);
            currText = pages.getCurrentPage().getElementText(elementName);
        }
        assertThat("[ERROR] Text of the element " + elementName + ": '" + currText + "' doesn't contain " +
                        "substring '" + text + "'.",
                currText, not(containsString(text)));
    }

    @Then("element $elementName contains $text")
    public void elementContainsText(String elementName, String text) {
        pages.getCurrentPage().waitForElementContainsText(elementName, text, DEFAULT_TIMEOUT_IN_SECONDS);
        String currText = "";
        try {
            currText = pages.getCurrentPage().getElementText(elementName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForElementContainsText(elementName, text, DEFAULT_TIMEOUT_IN_SECONDS);
            currText = pages.getCurrentPage().getElementText(elementName);
        }
        assertThat("[ERROR] Text of the element " + elementName + ": '" + currText + "' doesn't contain " +
                        "substring '" + text + "'.",
                currText, containsString(text));
    }

    @Then("current page URL contains $text")
    public void currentPageUrlContainsText(String text) {
        assertThat("[ERROR] Current page URL does not contain text: " + text + ", " +
                "actual url is " + pages.getCurrentPage().getCurrentUrl(),
                pages.getCurrentPage().getCurrentUrl(), containsString(text));
    }

    /**
     * Search every block on the current page for HTML element then click it
     * priority=0
     *
     * @param elementName HTML element name from a Story
     */
    @When("customer press Enter on $elementName")
    public void pressEnter(String elementName) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, false);
        pages.getCurrentPage().waitForElementDisplayed(elementName, DEFAULT_TIMEOUT_IN_SECONDS);
        try {
            pages.getCurrentPage().pressEnter(elementName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitForElementDisplayed(elementName, DEFAULT_TIMEOUT_IN_SECONDS);
            pages.getCurrentPage().pressEnter(elementName);
        }
    }


    @Then("customer sees number=$number of $elementName")
    public void checkNumberOfElements(int number, String elementName) {
        int curNumber = pages.getCurrentPage().getElementsSize(pages.getCurrentPage().getElementLocatorByName(elementName));
        assertThat("[ERROR] Expected number of elements is " + number + ", but current number is " + curNumber,
                curNumber, is(number));
    }


    /**
     * Get element's color {@link com.griddynamics.qa.ui.CommonElementMethods#getElementCssAttributeValue(java.lang.String, java.lang.String)}
     * Check that element's color is equal to expected from story
     * {@link com.griddynamics.qa.ui.CommonElementMethods#getElementCssAttributeValue    }
     *
     *@param elementName elementName
     * @param color       Possible values "Red","Grey"
     */
    @Then("$elementName element is highlighted with $color")
    public void checkColor(String elementName, String color) {
        HashMap<String, String> hmColorMap = new HashMap<String, String>();

        // Put more colors to the map if necessary
        hmColorMap.put("Red", "rgba(255, 0, 0, 1)");
        hmColorMap.put("Grey", "rgba(95, 95, 95, 1)");

        try {
            String hColor = hmColorMap.get(color);

            assertTrue("[ERROR] Element " + elementName + " is not displayed on page", pages.getCurrentPage().isElementDisplayedOnPage(elementName));

            String val = "";
            try {
                val = pages.getCurrentPage().getElementCssAttributeValue(elementName, "color");
            } catch (StaleElementReferenceException e) {
                pages.getCurrentPage().waitForElementDisplayed(elementName, DEFAULT_TIMEOUT_IN_SECONDS);
                val = pages.getCurrentPage().getElementCssAttributeValue(elementName, "color");
            }

            assertThat("[ERROR] Element color should be " + color + " but it have another color", val, is(hColor));
        } catch (NullPointerException npe) {

            assertTrue("<<< WARNING! Unknown color <<" + color + ">>! Please, add it in hpColorMap >>>" + npe.getMessage(), false);
        }
    }


    /**
     * Check that new page is opened in new Window and then close New Window
     *
     * see {link com.griddynamics.qa.ui.AbstractPage#checkNewWindowOpened}
     */
    @Then("new page is opened in new window")
    public void newPageIsOpenedInNewWindow() {
        pages.getCurrentPage().checkNewWindowOpened(pages.getBaseWindowHandle());
    }

    @Then("new page is opened in new window and new page URL contains text $text")
    public void newPageIsOpenedInNewWindowAndURLContainsText(String text) {
        pages.getCurrentPage().checkNewWindowUrlContainsText(pages.getBaseWindowHandle(), text);
    }

    @Given("customer returns to Current Page")
    public void returnsOnCurrentPage() {
        getLogger().info("Return to current page: ");
        pages.getCurrentPage().openPage();
    }

    /**
     * Gather information about current page {@link com.griddynamics.qa.ui.AbstractPage#gatherElements}.
     */
    @When("customer looks at current Page")
    public void whenCustomerLooksAtPage() {
        pages.getCurrentPage().gatherElements();
    }

    @Then("page title is $title")
    public void pageTitle(String title) {
        assertThat("[ERROR] Page title is incorrect", pages.getCurrentPage().getTitle(), is(title));
    }

    /**
     *@param elementName elementName
     * @param text        expected label text from story
     */
    @Then(value = "label $elementName has text $text", priority = 2)
    public void labelHasText(@Named("elementName") String elementName, @Named("text") String text) {
        String labelText = "";
        try {
            labelText = pages.getCurrentPage().getTextOfHiddenElement(elementName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitUntilNotStaleElement(pages.getCurrentPage().getElementLocatorByName(elementName, true, false),
                    DEFAULT_TIMEOUT_IN_SECONDS);
            labelText = pages.getCurrentPage().getTextOfHiddenElement(elementName);
        }
        labelText = labelText.replace("\n", "").replace("\t", "").replaceAll("<!--.*-->", "").trim();
        assertThat("[ERROR] Label " + elementName + " has wrong label text", labelText, is(text));
    }

    @Then(value = "attribute $attrName of label element $elementName contains $attrValue", priority = 2)
    public void hiddenElementHasAttributeWithValue(String attrName,
                                                   String elementName,
                                                   String attrValue) {
        String currentAttrValue = "";
        try {
            currentAttrValue = pages.getCurrentPage().getAttrValueOfHiddenElement(elementName, attrName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitUntilNotStaleElement(pages.getCurrentPage().getElementLocatorByName(elementName, true, false),
                    DEFAULT_TIMEOUT_IN_SECONDS);
            currentAttrValue = pages.getCurrentPage().getAttrValueOfHiddenElement(elementName, attrName);
        }
        assertThat("[ERROR] Element " + elementName + " has wrong attribute " + attrName + " value",
                currentAttrValue, containsString(attrValue));
    }

    @Then("Selector $elementName has element with text $text on $number place")
    public void selectorOptionHasText(String elementName, String text, int number) {
        String optionText = "";
        try {
            optionText = pages.getCurrentPage().getDropDownElementText(elementName, number);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitUntilNotStaleElement(pages.getCurrentPage().getElementLocatorByName(elementName, true, true),
                    DEFAULT_TIMEOUT_IN_SECONDS);
            optionText = pages.getCurrentPage().getDropDownElementText(elementName, number);
        }
        assertThat("[ERROR] Options value in selector " + elementName + " located at " +
                number + " place has wrong text", optionText, is(text));
    }

    @Then("first element of $elementName is selected")
    public void firstElementIsSelected(String elementName) {
        boolean isSelected = false;
        try {
            isSelected = pages.getCurrentPage().isFirstElementSelected(elementName);
        } catch (StaleElementReferenceException e) {
            pages.getCurrentPage().waitUntilNotStaleElement(pages.getCurrentPage().getElementLocatorByName(elementName, true, false),
                    DEFAULT_TIMEOUT_IN_SECONDS);
            isSelected = pages.getCurrentPage().isFirstElementSelected(elementName);
        }
        assertTrue("[ERROR] First element should be selected in " + elementName + ", but it is not", isSelected);
    }

    @Then("Attribute $attribute of $number option in $elementName selector has text $text")
    public void selectorAttrHasText(String attribute, int number, String elementName, String text) {
        String optionText = "";
        try {
            optionText = pages.getCurrentPage().getAttributeValueFromDropDownElement(elementName, attribute, number);
        } catch (StaleElementReferenceException e){
            pages.getCurrentPage().waitUntilNotStaleElement(pages.getCurrentPage().getElementLocatorByName(elementName, true, false),
                    DEFAULT_TIMEOUT_IN_SECONDS);
            optionText = pages.getCurrentPage().getAttributeValueFromDropDownElement(elementName, attribute, number);
        }
        assertThat("[ERROR] Options attribute " + attribute + " in selector " + elementName + " located at " +
                number + " place has wrong text", optionText, is(text));
    }

    /**
     *@param blockName blockName
     * @see CommonPageSteps#waitForBlockForTime(java.lang.String, java.lang.Integer)
     *      timeout {@link CommonPageSteps#DEFAULT_TIMEOUT_IN_SECONDS}
     */
    @Then("customer waits when block $blockName appears")
    public void waitForBlock(String blockName) {
        waitForBlockForTime(blockName, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    /**
     *@param elementName elementName
     * @see com.griddynamics.qa.ui.steps.CommonPageSteps#waitForElement
     *      timeout {@link CommonPageSteps#DEFAULT_TIMEOUT_IN_SECONDS}
     */
    @Then("customer waits when element $element appears")
    public void waitForElement(String elementName) {
        waitForElement(elementName, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    /**
     * waits during timeout while block will be displayed
     *
     *@param blockName blockName
     * @param timeout   in seconds
     */
    @Then("customer waits when block $blockName appears for $timeout second{s|}")
    public void waitForBlockForTime(String blockName, Integer timeout) {
        pages.getCurrentPage().waitForBlockDisplayed(blockName, timeout);
    }

    /**
     * waits during timeout while element will be displayed
     *
     *@param elementName elementName
     * @param timeout     in seconds
     */
    @Then("customer waits when element $element appears for $timeout second{s|}")
    public void waitForElement(String elementName, Integer timeout) {
        pages.getCurrentPage().waitForElementDisplayed(elementName, timeout);
    }

    /**
     * @deprecated use waitForElement(String elementName)
     * waits during timeout {@link com.griddynamics.qa.ui.CommonElementMethods#DEFAULT_TIMEOUT_IN_SECONDS} while element will be load and displayed
     * Element can absent on page when waiting is started
     *
     *@param elementName elementName
     */
    @Deprecated
    @Then("customer waits when element $element load and will be displayed")
    public void waitWhileElementLoadAndDisplayed(String elementName) {
        boolean isDisplayed = pages.getCurrentPage().isElementDisplayedAndClickable(elementName);
        assertTrue("[ERROR] Element was not loaded during timeout: " + elementName, isDisplayed);
    }

    /**
     * @deprecated use waitAllScriptsFinish() instead
     * waits {value #WAIT_ELEMENT_LOAD_TIMEOUT_IN_SEC} seconds while Java Scripts will be completed
     * time steps waiting {value #WAIT_LOAD_TIMEOUT_IN_MS}
     */
    @Deprecated
    @Then("customer waits while all ajax scripts will be completed")
    public void waitAllAjaxWillFinished () {
        int currentStepNumber = 0;
        int stepsCount = DEFAULT_TIMEOUT_IN_SECONDS*2;

        getLogger().info("Wait for DOM readiness");

        while ((!pages.getCurrentPage().isAjaxJQueryReady()) && (currentStepNumber < stepsCount * 2))
        {
            currentStepNumber++;
            pages.getCurrentPage().sleep(WAIT_LOAD_TIMEOUT_IN_MS);
        }

        getLogger().info("DOM is ready, start to wait for ajax script");

        pages.getCurrentPage().sleep(WAIT_LOAD_TIMEOUT_IN_MS * 2);
        currentStepNumber += 2;

        while ((!pages.getCurrentPage().isAjaxJQueryCompleted()) && (currentStepNumber < stepsCount * 2))
        {
            currentStepNumber++;
            pages.getCurrentPage().sleep(WAIT_LOAD_TIMEOUT_IN_MS);
        }
        assertTrue("[ERROR] Scripts were not completed during timeout" + (DEFAULT_TIMEOUT_IN_SECONDS*2) +" seconds",
                pages.getCurrentPage().isAjaxJQueryCompleted());
    }

    @Then(value = "wait until all page scripts are completed")
    public void waitAllScriptsFinish() {
        pages.getCurrentPage().waitAllScriptsFinish(DEFAULT_TIMEOUT_IN_SECONDS*2);
    }

    /**
     * waits during timeout {@link com.griddynamics.qa.ui.CommonElementMethods#DEFAULT_TIMEOUT_IN_SECONDS} while element will be load and clicable
     * Element can absent on page when waiting is started
     * @deprecated  use waitElementClickable(String elementName)
     *@param elementName elementName
     */
    @Deprecated
    @Then("customer waits when element $element will be clickable")
    public void waitWhileElementIsClickable(String elementName) {
        By loc = pages.getCurrentPage().getElementLocatorByName(elementName, true, false);
        boolean isClickable = pages.getCurrentPage().isElementLoaded(elementName);
        assertTrue("[ERROR] Element was not loaded during timeout: " + elementName, isClickable);
    }


    @Then("$elementName width equals to $sizeValue")
    public void checkElementWidth(String elementName, int sizeValue) {
        assertThat("[ERROR] Element " + elementName + " width should be equal to " + sizeValue,
                pages.getCurrentPage().getElementByName(elementName).getSize().getWidth(), is(sizeValue));

    }

    @Then("$elementName width is more than $sizeValue")
    public void checkElementWidthMoreThan(String elementName, int sizeValue) {
        int actualWidth = pages.getCurrentPage().getElementByName(elementName).getSize().getWidth();
        assertThat("[ERROR] Element " + elementName + " width should be more than " + sizeValue +
                "; Actual value " + actualWidth, actualWidth, greaterThan(sizeValue));
    }

    @Then("$elementName height equals to $sizeValue")
    public void checkElementHeight(String elementName, int sizeValue) {
        assertThat("[ERROR] Element " + elementName + " height should be equal to " + sizeValue,
                pages.getCurrentPage().getElementByName(elementName).getSize().getHeight(), is(sizeValue));
    }

    @Then("$elementName height is more than $sizeValue")
    public void checkElementHeightMoreThan(String elementName, int sizeValue) {
        int actualHeight = pages.getCurrentPage().getElementByName(elementName).getSize().getHeight();
        assertThat("[ERROR] Element " + elementName + " height should be more than " + sizeValue +
                "; Actual value " + actualHeight, actualHeight, greaterThan(sizeValue));
    }

    @When(value = "customer hovers and holds mouse over $elementName1 and then click on $elementName2")
    public void hoverToElementAndClick(String elementName1, String elementName2) throws InterruptedException {
        scrollToElement(elementName1);
        pages.getCurrentPage().hoverOverTheElement(elementName1);
        Thread.sleep(WAIT_LOAD_TIMEOUT_IN_MS);
        scrollToElement(elementName2);
        pages.getCurrentPage().click(elementName2);
    }

    @When("customer scrolls to the element $elementName")
    public void scrollToElement(String elementName) {
        pages.getCurrentPage().scrollToElement(pages.getCurrentPage().getElementByName(elementName));
    }

    @When("close modal window")
    public void closeModalWindow() {
        pages.getCurrentPage().switchTo().alert().accept();

    }

    @Then("modal window contains $text")
    public void closeModalWindow(String text) {
        String textFromModal = pages.getCurrentPage().switchTo().alert().getText();
        assertTrue("Modal Message does not contain expected text.  Expected: '" + text + "'; Got: " + textFromModal,
                textFromModal.contains(text));
    }

    @When("customer double clicks $elementName")
    public void doubleClick(String elementName) {
        new Actions(this.pages.getCurrentPage().getDriver()).moveToElement(this.pages.getCurrentPage().getElementByName(elementName)).doubleClick().perform();
    }

    @When("customer right clicks $elementName")
    public void rightClick(String elementName) {
        new Actions(this.pages.getCurrentPage().getDriver())
                .moveToElement(this.pages.getCurrentPage().getElementByName(elementName))
                .contextClick().build().perform();
    }

    /* Methods waiting for expected conditions */



    @Then("wait until attribute $attrName of element $elementName contains $attrValue")
    public void waitElementContainsStringInAttribute(String attrName,
                                                     String elementName,
                                                     String attrValue) {
        pages.getCurrentPage().waitForElementAttributeValue(elementName, attrName, attrValue, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    @Then("wait until element $elementName contains $text")
    public void waitElementContainsText(String name,
                                        String text) {
        pages.getCurrentPage().waitForElementContainsText(name, text, DEFAULT_TIMEOUT_IN_SECONDS);

    }

    @Then("wait until element $elementName is clickable")
    public void waitElementClickable(String elementName) {
        pages.getCurrentPage().waitForElementClickable(elementName, DEFAULT_TIMEOUT_IN_SECONDS);
    }

}

