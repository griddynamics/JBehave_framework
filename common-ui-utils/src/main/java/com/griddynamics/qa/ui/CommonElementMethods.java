package com.griddynamics.qa.ui;


import com.griddynamics.qa.tools.StringParser;
import com.griddynamics.qa.ui.utils.CustomExpectedConditions;
import com.griddynamics.qa.ui.utils.TimeoutConstants;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.web.selenium.WebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

/**
 * @author ybaturina
 * @author mlykosova
 * @author aluchin
 *         <p/>
 *         Entity class containing common methods that work with page elements
 */

public class CommonElementMethods extends WebDriverPage implements TimeoutConstants{

    /**
     * blocks is the container of blocks aggregated by block/page
     */
    private List<ElementBlock> blocks = new ArrayList<ElementBlock>();

    /**
     * elementsMap is the container of elements aggregated by block or page
     */
    private Map<String, By> elementsMap = new HashMap<String, By>();

    public CommonElementMethods(WebDriverProvider driverProvider) {
        super(driverProvider);
    }

    public WebDriver getDriver() {
        return getDriverProvider().get();
    }

    public Map<String, By> getElementsMap() {
        return elementsMap;
    }

    public void setElementsMap(Map<String, By> elementsMap) {
        this.elementsMap.putAll(elementsMap);
    }

    public void addBlock(ElementBlock block) {
        blocks.add(block);
    }

    public void addElement(String name, By loc) {
        elementsMap.put(name, loc);
    }

    public List<ElementBlock> getBlockList() {
        return blocks;
    }

    /**
     * Recursively search a block by its name
     *
     * @param blockName
     * @return block with blockName or null if it doesn't exist
     */
    public ElementBlock getBlockByName(String blockName) {
        ElementBlock bl = null;
        for (ElementBlock block : getBlockList()) {
            if (StringUtils.isNotEmpty(block.getName()) && block.getName().equals(blockName)) {
                return block;
            } else {
                bl = block.getBlockByName(blockName);
                if (bl != null) {
                    return bl;
                }
            }
        }
        return bl;
    }

    /**
     * Searches web element by its name
     *
     * @param name
     * @return webElement
     * @throws RuntimeException when unable to find the element
     */
    public WebElement getElementByName(String name) {
        return initElement(name);
    }

    public By getMandatoryElementLocatorByName(String name) {
        By elLoc = getLocatorByName(name);
        assertThat("[ERROR] Element " + name + " was not found in the page and its block classes", elLoc, not(nullValue()));
        return elLoc;
    }

    /**
     * Get element locator by name
     *
     * @param name name of the element
     * @param isFound flag indicating whether the element should be found in page classes or not
     * @param shouldPresent flag indicating whether the element locator should present in page DOM structure or not
     * @return By or null when can't find it
     * @throws RuntimeException when unable to find the element
     */
    public By getElementLocatorByName(String name, boolean isFound, boolean shouldPresent) {
        By elementLocator = (isFound) ? getMandatoryElementLocatorByName(name) : getLocatorByName(name);
        if (shouldPresent) {
            assertTrue("[ERROR] Element " + name + " with locator " + elementLocator + " is not present on page",
                    elementLocator != null && isLocatorPresentOnPage(elementLocator));
        }
        return elementLocator;
    }

    /**
     * Get Element locator by name for element which present on page or it blocks
     *
     * @param name
     * @return
     * @see CommonElementMethods#getElementLocatorByName(java.lang.String, boolean, boolean)
     */
    public By getElementLocatorByName(String name) {
        return getElementLocatorByName(name, true, true);
    }

    /**
     * At first perform search Web Element on current page and if success return locator
     * Else recursively search in all blocks on current page while first elementName will be found and returns it locator
     *
     * @param elementName
     * @return locator
     */
    public By getLocatorByName(String elementName) {
        Map<String, By> elementMap = getElementsMap();
        By temp = elementMap.get(elementName);
        if (temp != null) {
            return elementMap.get(elementName);

        }

        for (ElementBlock block : getBlockList()) {
            temp = block.getLocatorByName(elementName);
            if (temp != null) {
                return temp;
            }
        }
        return temp;
    }

    /**
     * Searches for element name by the pattern
     *
     * @param nameMatch
     * @param resultMap
     * @return
     */
    public Map<String, By> getElementsByNameMatch(String nameMatch, Map<String, By> resultMap) {
        Map<String, By> result = new HashMap<String, By>();
        for (Map.Entry<String, By> entry : getElementsMap().entrySet()) {
            if (entry.getKey().matches(nameMatch)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        for (ElementBlock block : getBlockList()) {
            result.putAll(block.getElementsByNameMatch(nameMatch, result));
        }

        return result;
    }

    /**
     * Get element locator and type by its name then initialize element
     *
     * @param name
     * @return
     */
    public WebElement initElement(String name) {
        By locator = getElementLocatorByName(name);
        return findElementSuppressAlert(locator);
    }

    /**
     * Checks if dropdown has the first option selected
     *
     * @param name
     * @return
     */
    public boolean isFirstElementSelected(String name) {
        Select sel = new Select(getElementByName(name));
        return (getSelectedText(name).equals(sel.getOptions().get(0).getText())) ? true : false;
    }

    public String getDropDownElementText(String selectorName, int elementNum) {
        Select sel = new Select(getElementByName(selectorName));
        List<WebElement> options = sel.getOptions();
        return options.get(elementNum).getText();
    }

    public String getAttributeValueFromDropDownElement(String selectorName, String attr, int elementNum) {
        Select sel = new Select(getElementByName(selectorName));
        List<WebElement> options = sel.getOptions();
        return options.get(elementNum).getAttribute(attr);
    }

    public void click(WebElement el) {
        el.click();
    }

    public String getElementAttributeValue(String name, String attr) {
        String val = getElementByName(name).getAttribute(attr).toString();
        return (val == null) ? null : (val.equals("null") ? "" : val);
    }

    public String getLocatorAttributeValue(By loc, String attr) {
        if (isLocatorPresentOnPage(loc)) {
            String val = findElementSuppressAlert(loc).getAttribute(attr).toString();
            return val.equals("null") ? "" : val;
        } else return "";
    }

    /**
     * @param by   - element locator
     * @param attr - attribute name
     * @return - list of attributes values
     */
    public ArrayList<String> getAttributeValueForAllElements(By by, String attr) {
        List<WebElement> elements = findElements(by);
        assertThat("[ERROR] Can not find any element with locator: " + by, elements.size(), greaterThan(0));
        ArrayList<String> attributes = new ArrayList<String>();
        for (WebElement element : elements) {
            attributes.add(element.getAttribute(attr));
        }
        assertFalse("Can not find such attribute: " + attr, attributes.contains(null));
        return attributes;
    }

    public String getElementValue(String name) {
        String val = getElementByName(name).getAttribute("value").toString();
        return val.equals("null") ? "" : val;
    }

    public String getElementText(String name) {
        String val = getElementByName(name).getText().toString();
        return val.equals("null") ? "" : val;
    }

    public String getElementTextByLocator(By loc) {
        if (!isLocatorPresentOnPage(loc)) {
            throw new RuntimeException("Locator " + loc + " was not found on the page");
        }

        String elementText = findElementSuppressAlert(loc).getText();
        return elementText.equals("null") ? "" : elementText;
    }

    public boolean isElementEmpty(String name) {
        return StringUtils.isEmpty(getElementText(name))
                && StringUtils.isEmpty(getElementValue(name));
    }

    public String getSelectedValue(String name) {
        Select sel = new Select(getElementByName(name));
        return sel.getFirstSelectedOption().getAttribute("value");
    }

    public String getSelectedText(String name) {
        Select sel = new Select(getElementByName(name));
        return sel.getFirstSelectedOption().getText();
    }

    public void clearField(String name) {
        WebElement el = getElementByName(name);
        el.clear();
        assertThat("Field should be clear", el.getText(), isEmptyString());
    }

    /**
     * @param name
     * @param text
     */
    public void typeText(String name, String text) {
        clearField(name);
        WebElement el = getElementByName(name);
        el.sendKeys(text);
    }

    public void typeTextInBlock(String blockName, String elementName, String text) {
        ElementBlock block = getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("Block with name " + blockName
                    + " was not found in page class");
        }
        block.typeText(elementName, text);
    }

    public void selectTextInBlock(String blockName, String elementName, String text) {
        ElementBlock block = getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("Block with name " + blockName
                    + " was not found in page class");
        }
        block.selectText(elementName, text);
    }

    public boolean isChecked(String name) {
        return getElementByName(name).isSelected();
    }

    public void click(String name) {
        getElementByName(name).click();
    }

    public void submit(String name) {
        WebElement el = getElementByName(name);
        if (System.getProperty("browser").equals("htmlunit")) {
            el.submit();
        } else {
            el.click();
        }
    }

    public void submitButton(String name) {
        WebElement el = getElementByName(name);
        el.submit();
    }


    public Select select(By loc) {
        return (isLocatorPresentOnPage(loc)) ? new Select(findElementSuppressAlert(loc)) : null;
    }

    public void selectText(String elementName, String text) {
        Select sel = new Select(getElementByName(elementName));
        if (StringUtils.isNotEmpty(text)) {
            sel.selectByVisibleText(text);
        } else {
            sel.selectByIndex(0);
        }
    }

    public void selectValue(String elementName, String value) {
        Select sel = new Select(getElementByName(elementName));
        if (StringUtils.isNotEmpty(value)) {
            sel.selectByValue(value);
        } else {
            sel.selectByIndex(0);
        }
    }

    /**
     * Check element isSelected and if it not equal to val then click on it
     *
     * @param elementName
     * @param val
     */
    public void setCheckbox(String elementName, boolean val) {
        WebElement el = getElementByName(elementName);
        if (val != el.isSelected()) {
            el.click();
        }
    }

    public boolean isElementPresentOnPage(String name) {
        return findElements(getElementLocatorByName(name)).size() > 0;
    }

    public boolean isElementDisplayedOnPage(String name) {
        By loc = getElementLocatorByName(name);
        waitForLocatorVisibility(loc, DEFAULT_TIMEOUT_IN_SECONDS);
        try {
            return isLocatorPresentOnPage(loc) ? findElementSuppressAlert(loc).isDisplayed() : false;
        } catch (StaleElementReferenceException e) {
            waitForLocatorVisibility(loc, DEFAULT_TIMEOUT_IN_SECONDS);
            return isLocatorPresentOnPage(loc) ? findElementSuppressAlert(loc).isDisplayed() : false;
        }
    }

    public boolean isLocatorPresentOnPage(By loc) {
        return loc!=null?getElementsSize(loc) > 0:false;
    }

    public boolean isLocatorDisplayedOnPage(By loc, boolean shouldDisplay, int timeout) {
        assertNotNull("[ERROR] Locator is null", loc);
        if (shouldDisplay) {
            waitForLocatorVisibility(loc,timeout);
        }
        try {
            return isLocatorPresentOnPage(loc) ? findElementSuppressAlert(loc).isDisplayed() : false;
        } catch (StaleElementReferenceException e) {
            if (shouldDisplay) {
                waitForLocatorVisibility(loc, timeout);
            }
            else{
                waitUntilNotStaleElement(loc, timeout);
            }
            return isLocatorPresentOnPage(loc) ? findElementSuppressAlert(loc).isDisplayed() : false;
        }
    }

    public boolean isLocatorDisplayedOnPage(By loc) {
        return isLocatorDisplayedOnPage(loc, true, DEFAULT_TIMEOUT_IN_SECONDS);
    }


    /**
     * Wait {@value #DEFAULT_TIMEOUT_IN_SECONDS} seconds while element will be displayed
     *
     * @param name - element name
     * @return
     */
    public boolean isElementLoaded(String name) {
        By loc = getElementLocatorByName(name, false, false);
        WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), DEFAULT_TIMEOUT_IN_SECONDS);
        WebElement element = wait.until(CustomExpectedConditions.visibilityOfElementLocated(loc));
        return element.isDisplayed();
    }

    /**
     * Wait {@value #DEFAULT_TIMEOUT_IN_SECONDS} seconds while hidden element will be displayed
     *
     * @param loc - elements locator
     * @return each WebElement.isEnabled
     */
    public boolean areElementsLoaded(By loc){
        WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), DEFAULT_TIMEOUT_IN_SECONDS);
        List<WebElement> presents = wait.until(presenceOfAllElementsLocatedBy(loc));
        for (WebElement present : presents){
            if (!present.isDisplayed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Wait {@value #DEFAULT_TIMEOUT_IN_SECONDS} seconds while element will be displayed and clickable
     *
     * @param name
     * @return
     */
    public boolean isElementDisplayedAndClickable(String name) {
        waitForElementClickable(name, DEFAULT_TIMEOUT_IN_SECONDS);
        WebElement element = findElementSuppressAlert(getElementLocatorByName(name));
        return element.isDisplayed() && element.isEnabled();
    }
    
    /**
     * Check that jQuery is defined and ready
     *
     * @return true if window.jQuery != undefined && jQuery.isReady == 1
     */
    public boolean isAjaxJQueryReady() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        Boolean scriptResult = (Boolean) js.executeScript("return window.jQuery != undefined && jQuery.isReady == 1", new Object[0]);
        return scriptResult.booleanValue();
    }


    /**
     * Check that there is no active jQuery session
     *
     * @return true if jQuery.active == 0
     */
    public boolean isAjaxJQueryCompleted() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        Boolean scriptResult = (Boolean) js.executeScript("return jQuery.active == 0", new Object[0]);
        return scriptResult.booleanValue();
    }


    /**
     * Wait {@value #DEFAULT_TIMEOUT_IN_SECONDS} seconds while hidden element will be present
     *
     * @param loc - element locator
     * @return WebElement.isEnabled
     */
    public boolean isHiddenElementLoaded(By loc) {
        WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), DEFAULT_TIMEOUT_IN_SECONDS);
        WebElement present = wait.until(CustomExpectedConditions.presenceOfElementLocated(loc));
        return present.isEnabled();
    }


    public int getElementsSize(By loc) {
        return findElements(loc).size();
    }

    /**
     * @deprecated use findElements(By loc) instead
     * @param loc
     * @return
     */
    @Deprecated
    public List<WebElement> getElementsByLoc(By loc) {
        return findElements(loc);
    }


    /**
     * <p/>
     * Method used to Click and Hold the Element
     */
    public void hoverOverHold(String name1) {
        By loc = getElementLocatorByName(name1);
        WebElement el1 = findElementSuppressAlert(loc);
        Actions builder = new Actions(getDriver());
        builder.clickAndHold(el1).build().perform();
        try {
            Thread.sleep(WAIT_LOAD_TIMEOUT_IN_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            assertTrue("[ERROR] Interrupted exception was thrown during timeout: " + e.getMessage(), false);
        }
    }

    /**
     * <p/>
     * Method used to switch the focus to Alert Box
     */
    public boolean isAlertPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), DEFAULT_TIMEOUT_IN_SECONDS);
            wait.until(alertIsPresent());
            switchTo().alert();
            return true;
        } catch (Exception Ex) {
            return false;
        }
    }

    public void closeAlert() {
        switchTo().alert().accept();
    }

    /**
     * Method is used to get the data from HTML table
     * @param name
     * @param rowNum
     * @param columnNum
     * @return
     */
    public String getTableData(String name, int rowNum, int columnNum) {
        WebElement table = getElementByName(name);
        // Now get all the TR elements from the table
        List<WebElement> allRows = table.findElements(By.tagName("tr"));
        // And iterate over them, getting the cells
        int rowCount = 1;
        int columnCount = 1;
        for (WebElement row : allRows) {
            if (rowNum == rowCount) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                for (WebElement cell : cells) {
                    if (columnNum == columnCount) {
                        return cell.getText();
                    }
                    columnCount++;
                }
            }
            rowCount++;
        }
        return null;
    }


    /**
     * Clicks on element with javascript
     *
     * @param name
     */
    public void clickElementWithScript(String name) {
        By loc = getElementLocatorByName(name);
        clickElementWithScriptByLoc(loc);
    }


    /**
     * Clicks on element by locator with javascript
     *
     * @param loc
     */
    public void clickElementWithScriptByLoc(By loc) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        if (isLocatorPresentOnPage(loc)) {
            WebElement hiddenElement = findElementSuppressAlert(loc);
            js.executeScript("arguments[0].click()", hiddenElement);
        }
    }


    /**
     * Get text of hidden element with javascript
     *
     * @param name
     */
    public String getTextOfHiddenElement(String name) {
        return runJavaScriptForElement(name, "innerHTML");
    }


    /**
     * Get attribute value of hidden element (or element that cannot be initialized
     * using WebElement) with javascript
     *
     * @param name
     */
    public String getAttrValueOfHiddenElement(String name, String attribute) {
        return runJavaScriptForElement(name, "getAttribute(\"" + attribute + "\")");
    }


    private String runJavaScriptForElement(String name, String javascript) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        By loc = getElementLocatorByName(name);
        String elementText = null;
        WebElement hiddenElement = findElementSuppressAlert(loc);
        elementText = ((String) js.executeScript("return arguments[0]." + javascript + ";", hiddenElement)).trim();
        elementText = ridOfAttributes(elementText).replace("&amp;", "&");
        return elementText;
    }


    public String runJavaScriptForCurrentPage(String javascript) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return (String) js.executeScript(javascript);
    }


    /**
     * Get text of hidden element with javascript
     */
    public String getTextOfHiddenElementByLoc(By loc) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        String elementText = null;
        if (isLocatorPresentOnPage(loc)) {
            WebElement hiddenElement = findElementSuppressAlert(loc);
            elementText = ((String) js.executeScript("return arguments[0].innerHTML;", hiddenElement)).trim();
            elementText = ridOfAttributes(elementText).replace("&amp;", "&");
        }

        return elementText;
    }

    /**
     * Get html source of element with javascript
     *
     * @param elementName
     */
    public String getHTMLSourceOfElement(String elementName) {
        return getHTMLSource(elementName, true);

    }

    /**
     * Get html source of block with javascript
     *
     * @param blockName
     */
    public String getHTMLSourceOfBlock(String blockName) {
        return getHTMLSource(blockName, false);
    }

    private String getHTMLSource(String name, boolean isElement) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        By loc = isElement? getElementLocatorByName(name) : getBlockByName(name).getLocator();
        String elementText = null;
        WebElement element = findElementSuppressAlert(loc);
        elementText = ((String) js.executeScript("return arguments[0].innerHTML;", element)).trim();
        return elementText;
    }

    private String ridOfAttributes(String text) {
        if (text.contains("\">")) {
            text = StringParser.getMatchString(text, "(?<=\">).*");
        }

        return text.trim();
    }

    public void pressEnter(String elName) {
        getElementByName(elName).sendKeys(Keys.ENTER);
    }

    /**
     * Search a block containing HTML element
     *
     * @param elName HTML element name from a Story
     * @return
     */
    public ElementBlock findBlockWithElement(String elName) {
        for (ElementBlock block : getBlockList()) {
            if (block.getLocatorByName(elName) != null) {
                return block;
            }
        }

        throw new RuntimeException("Element with name: " + elName
                + " was not initialized in blocks");
    }

    /**
     * looking for HTML element with elName in the all blocks of the current page
     *
     * @param blockName -  fragment of the page where HTML element will be looked up
     * @param elName    - HTML element name from a Story
     * @return WebElement if element with elName was found on the page
     * @throws throws RuntimeException when HTML element or block may not be found
     */
    public WebElement findElementInBlock(String blockName, String elName) {
        ElementBlock elBlock = getBlockByName(blockName);
        if (elBlock == null) {
            throw new RuntimeException("Block with name " + blockName
                    + " was not found in page class");
        }
        try {
            return elBlock.getElementByName(elName);
        } catch (RuntimeException e) {
            throw new RuntimeException("Element with name: " + elName
                    + " was not initialized in block " + blockName);
        }
    }

    /**
     * @param elementName
     * @param attributeName
     * @return
     */
    public String getElementCssAttributeValue(String elementName, String attributeName) {
        String val = getElementByName(elementName).getCssValue(attributeName).toString();
        return val.equals("null") ? "" : val;
    }

    /**
     * Suppresses alert pop up if it throws exception
     *
     * @param loc
     * @return
     */
    protected WebElement findElementSuppressAlert(By loc) {
        try {
            return findElement(loc);
        } catch (UnhandledAlertException e) {
            switchTo().alert().dismiss();
            switchTo().defaultContent();
            WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), DEFAULT_TIMEOUT_IN_SECONDS);
            if (!wait.until(ExpectedConditions.not(alertIsPresent()))) {
                assertTrue("[ERROR] Exception in waiting for alert to disappear:" + switchTo().alert().getText(), false);
            }
            return findElement(loc);
        }
    }

    /**
     * @param elements list of WebElements
     * @return WebElement
     */
    public static WebElement getRandomWebElement(List<WebElement> elements) {
        int size = elements.size();
        assertThat("[ERROR] Cannot select random element from empty list.", size, greaterThan(0));
        Random randomGenerator = new Random();
        return elements.get(randomGenerator.nextInt(size));
    }


    /**
     * @param loc
     */
    public void switchToIframeWithLoc(By loc) {
        try {
            WebElement element = findElementSuppressAlert(loc);
            if (StringUtils.isEmpty(element.getAttribute("id"))) {
                switchTo().frame(findElementSuppressAlert(loc));
            } else {
                switchTo().frame(findElementSuppressAlert(loc).getAttribute("id"));
            }
        } catch (NoSuchFrameException e) {
            assertTrue("[ERROR] Element with locator " + loc + " is not an iframe:" + e.getMessage(), false);
        }

    }

    /**
     * Scroll to the element on page
     * @param element  - WebElement
     */
    public void scrollToElement(WebElement element) {
        ((Locatable)element).getCoordinates().inViewPort();
    }



    /**
     * Search HTML element by name
     * Scroll to it on display
     * And Hover Over The Element
     *
     * @param elName HTML element name from a Story
     */
    public void hoverOverTheElement(String elName){
        Actions action = new Actions(getDriverProvider().get());

        WebElement webElement = getElementByName(elName);
        scrollToElement(webElement);
        action.moveToElement(webElement).build().perform();
    }

    /*************** Methods waiting for expected conditions **********************/

    public void waitForLocatorClickable(By loc, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
        wait.until(CustomExpectedConditions.locatorToBeClickable(loc));
    }

    public void waitForElementClickable(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
        wait.until(CustomExpectedConditions.elementToBeClickable(element));
    }

    public void waitForElementClickable(String name, int timeout) {
        waitForLocatorClickable(getElementLocatorByName(name, true, false), timeout);
    }

    public void waitForLocatorVisibility(By loc, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), timeout);
        try {
            wait.until(CustomExpectedConditions.visibilityOfElementLocated(loc));
        } catch (TimeoutException e) {
            getLogger().info("[INFO] Locator " + loc + " is not present on page");
        }
    }

    public void waitAllScriptsFinish(int timeout) {;
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            waiter.until(CustomExpectedConditions.isDocumentReady());
        } catch (TimeoutException e) {
            getLogger().error("[ERROR] Scripts were not completed during timeout" + timeout + " seconds");
        }
    }

    public void waitForLocatorAttributeValue(By loc, String attr, String attrVal, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            waiter.until(CustomExpectedConditions.elementAttributeHasValue(loc, attr, attrVal));
        } catch (TimeoutException e) {
            String currentAttrValue = getLocatorAttributeValue(loc, attr);
            assertThat("[ERROR] Locator " + loc + " has attribute " + attr + " with value "
                    + currentAttrValue, currentAttrValue, containsString(attrVal));
        }
    }

    public void waitForLocatorAttributeNoValue(By loc, String attr, String attrVal, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            waiter.until(CustomExpectedConditions.elementAttributeHasNoValue(loc, attr, attrVal));
        } catch (TimeoutException e) {
            String currentAttrValue = getLocatorAttributeValue(loc, attr);
            assertThat("[ERROR] Locator " + loc + " has attribute " + attr + " with value "
                    + currentAttrValue, currentAttrValue, not(containsString(attrVal)));
        }
    }

    public void waitForElementCssAttributeValue(WebElement element, String attr, String attrVal, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            waiter.until(CustomExpectedConditions.elementCssAttributeHasValue(element, attr, attrVal));
        } catch (TimeoutException e) {
            String currentAttrValue = element.getCssValue(attr);
            assertThat("[ERROR] Element has attribute " + attr + " with value "
                    + currentAttrValue, currentAttrValue, containsString(attrVal));
        }
    }

    public void waitForElementCssAttributeNoValue(WebElement element, String attr, String attrVal, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            waiter.until(CustomExpectedConditions.elementCssAttributeHasNoValue(element, attr, attrVal));
        } catch (TimeoutException e) {
            String currentAttrValue = element.getCssValue(attr);
            assertThat("[ERROR] Element has attribute " + attr + " with value "
                    + currentAttrValue, currentAttrValue, containsString(attrVal));
        }
    }

    public void waitForElementAttributeValue(String name, String attr, String attrVal, int timeout) {
        waitForLocatorAttributeValue(getElementLocatorByName(name, true, true), attr, attrVal, timeout);
    }

    public void waitForElementContainsText(String name, String text, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            waiter.until(CustomExpectedConditions.elementContainsText(getElementLocatorByName(name, true, true), text));
        } catch (TimeoutException e) {
            String currentText = getElementText(name);
            assertThat("[ERROR] Element " + name + " has text "
                    + currentText, currentText, containsString(text));
        }
    }

    public void waitForElementContainsNoText(String name, String text, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            waiter.until(CustomExpectedConditions.elementContainsNoText(getElementLocatorByName(name, true, true), text));
        } catch (TimeoutException e) {
            String currentText = getElementText(name);
            assertThat("[ERROR] Element " + name + " has text "
                    + currentText, currentText, not(containsString(text)));
        }
    }

    public void waitForElementContainsValue(String name, String value, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        waiter.until(CustomExpectedConditions.elementAttributeHasValue(getElementLocatorByName(name, true, true), "value", value));
    }

    public void mandatoryWaitForElementContainsValue(String name, String value, int timeout) {
        try {
            waitForElementContainsValue(name, value, timeout);
        } catch (TimeoutException e) {
            String currentValue = getElementValue(name);
            assertThat("[ERROR] Element " + name + " has value "
                    + currentValue, currentValue, containsString(value));
        }
    }

    public void mandatoryWaitForElementClickable(String name, int timeout) {
        try {
            waitForElementClickable(name, timeout);
        } catch (TimeoutException e) {
            assertTrue("[ERROR] Element " + name + " is disabled", getElementByName(name).isEnabled());
        }
    }

    public void waitForBlockDisplayed(String blockName, int timeout) {
        ElementBlock block = getBlockByName(blockName);
        if (block == null) {
            throw new RuntimeException("Block with name " + blockName
                    + " was not found in page class");
        }
        By loc = block.getLocator();
        try {
            waitForLocatorDisplayed(loc, timeout);
        } catch (TimeoutException e) {
            assertTrue("[ERROR] Block " + blockName + " was not displayed during timeout - " + timeout, isLocatorDisplayedOnPage(loc));
        }
    }

    public void waitForElementDisplayed(String elementName, int timeout) {
        By loc = getElementLocatorByName(elementName, true, false);
        WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
        try {
            wait.until(CustomExpectedConditions.visibilityOfElementLocated(loc));
        } catch (TimeoutException e) {
            assertTrue("[ERROR] Element " + elementName + " was not displayed during timeout - " + timeout, isLocatorDisplayedOnPage(loc));
        }
    }

    public void waitForLocatorDisplayed(By loc, int timeout) {
        WebDriverWait wait = new WebDriverWait(getDriver(), timeout);
        try {
            wait.until(CustomExpectedConditions.visibilityOfElementLocated(loc));
        } catch (TimeoutException e) {
            assertTrue("[ERROR] Locator " + loc + " is not present on page", isLocatorDisplayedOnPage(loc));
        }
    }

    public void waitForElementsCount(By loc, int count, int timeout) {
        waitForElementsCount(loc, count, true, timeout);
    }

    public void waitForElementsCount(By loc, int count, boolean isEqual, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        try {
            if (isEqual) {
                waiter.until(CustomExpectedConditions.elementsHasCount(loc, count));
            } else {
                waiter.until(CustomExpectedConditions.elementsHasNotCount(loc, count));
            }
        } catch (TimeoutException e) {
            int currentCount = getElementsSize(loc);
            assertTrue("[ERROR] Locator " + loc + " has count " + currentCount, currentCount == count);
        }
    }

    public boolean waitUntilNotStaleElement(By loc, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        return waiter.until(CustomExpectedConditions.isNotStaleElement(loc));
    }

    public boolean waitUntilNotStaleElement(String name, int timeout) {
        WebDriverWait waiter = new WebDriverWait(getDriver(), timeout);
        return waiter.until(CustomExpectedConditions.isNotStaleElement(getElementLocatorByName(name)));
    }
}
