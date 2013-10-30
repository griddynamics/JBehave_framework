package com.griddynamics.qa.ui;


import com.griddynamics.qa.tools.StringParser;
import org.jbehave.web.selenium.WebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;

/**
 * @author ybaturina
 *         mlykosova
 *         <p/>
 *         Entity class containing common methods that work with page elements
 */

public class CommonElementMethods extends WebDriverPage {
    private final static int WAIT_AFTER_HOVER_TIMEOUT = 500;
    private final static int WAIT_ALERT_DISMISS = 1000;
    private static final long TIME_OUT_IN_SECONDS = 30;

    /**
     * blocks is the container of blocks aggregated by block/page
     */

    private List<ElementBlock> blocks = new ArrayList<ElementBlock>();

    /**
     * html elements is the container of elements aggregated by block or page
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
        for (String elementName : elementsMap.keySet()) {
            this.elementsMap.put(elementName, elementsMap.get(elementName));
        }
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
     * Search for a block by its name
     *
     * @param blockName
     * @return block with blockName or null if it doesn't exist
     */
    public ElementBlock getBlockByName(String blockName) {
        ElementBlock bl = null;
        for (ElementBlock block : getBlockList()) {
            if (block.getName() != null && block.getName().equals(blockName)) {
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
     * Searches for Fluent web element element by its name
     *
     * @param name
     * @return webElement
     * @throws RuntimeException when unable to find the element
     */
    public WebElement getElementByName(String name) {
        return initElement(name);
    }

    public By getElementLocatorByName(String name, boolean isFound, boolean shouldPresent) {
        Map<String, By> elementInfo = (isFound) ? getMandatoryElementInfoByName(name) : getElementInfoByName(name);
        for (Map.Entry<String, By> entry : elementInfo.entrySet()) {
            if (shouldPresent) {
                Assert.assertTrue("[ERROR] Element " + name + " with locator " + entry.getValue() + " is not present on page", isLocatorPresentOnPage(entry.getValue()));
            }
            return entry.getValue();
        }
        return null;
    }

    public By getElementLocatorByName(String name) {
        return getElementLocatorByName(name, true, true);
    }

    /**
     * Searches for HTML element by its name
     *
     * @param name
     * @return html element or null when can't find it
     */
    public Map<String, By> getElementInfoByName(String name){
        Map<String, By> elementInfo = new HashMap<String, By>();
        for (Map.Entry<String, By> entry : getElementsMap().entrySet()) {
            if (entry.getKey().equals(name)) {
                elementInfo.put(entry.getKey(), entry.getValue());
                return elementInfo;
            }
        }

        for (ElementBlock block : getBlockList()) {
            elementInfo = block.getElementInfoByName(name);
            if (!elementInfo.isEmpty()) {
                return elementInfo;
            }
        }

        return elementInfo;
    }

    public Map<String, By> getMandatoryElementInfoByName(String name) {
        Map<String, By> elInfo = getElementInfoByName(name);
        Assert.assertFalse("[ERROR] Element " + name + " was not found in the page and its block classes", elInfo.isEmpty());
        return elInfo;
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
     * Get element locator and type by its name then initialize element based on its type
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
        if (getSelectedText(name).equals(sel.getOptions().get(0).getText())) {
            return true;
        } else {
            return false;
        }
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

    public ArrayList<String> getAttributeValueForAllElements(By by, String attr) {
        List<WebElement> elements = findElements(by);
        Assert.assertTrue("Can not find any element with locator: " + by, elements.size() > 0);
        ArrayList<String> attributes = new ArrayList<String>();
        for (WebElement element : elements) {
            attributes.add(element.getAttribute(attr));
        }
        Assert.assertFalse("Can not find such attribute: " + attr, attributes.contains(null));
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
        return getElementText(name).equals("")
                && getElementValue(name).equals("");
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
        Assert.assertNotSame("Field should be clear", "", el.getText());
    }

    public void typeText(String name, String text) {
        clearField(name);
        WebElement el = getElementByName(name);
        el.sendKeys(text);
    }

    public boolean isChecked(String name) {
        return getElementByName(name).isSelected();
    }

    public void click(String name) {
        getElementByName(name).click();
    }

    public void clickAndSleep(String name, long timeout) {
        click(name);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Assert.assertTrue("Unexpected exception happens into clickAndSleep() method: " + e.getMessage(), false);
        }
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

    public void submitAndSleep(String name, int timeout) {
        submit(name);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Assert.assertTrue("Unexpected exception happens into submitAndSleep() method: " + e.getMessage(), false);
        }
    }

    public Select select(By loc) {
        return (isLocatorPresentOnPage(loc)) ? new Select(findElementSuppressAlert(loc)) : null;
    }

    public void selectText(String name, String text) {
        Select sel = new Select(getElementByName(name));
        if (!text.isEmpty()) {
            sel.selectByVisibleText(text);
        } else {
            sel.selectByIndex(0);
        }
    }

    public void selectValue(String name, String value) {
        Select sel = new Select(getElementByName(name));
        if (!value.isEmpty()) {
            sel.selectByValue(value);
        } else {
            sel.selectByIndex(0);
        }
    }

    public void setCheckbox(String name, boolean val) {
        WebElement el = getElementByName(name);
        if (val != el.isSelected()) {
            el.click();
        }
    }

    public boolean isElementPresentOnPage(String name) {
        return findElements(getElementLocatorByName(name)).size() > 0;
    }

    public boolean isElementDisplayedOnPage(String name) {
        By loc = getElementLocatorByName(name);

        return isLocatorPresentOnPage(loc) ? findElementSuppressAlert(loc).isDisplayed() : false;
       // return findElementSuppressAlert(loc).isDisplayed();
    }

    public boolean isLocatorPresentOnPage(By loc) {
        return getElementsSize(loc) > 0;
    }

    public boolean isLocatorDisplayedOnPage(By loc) {
        return isLocatorPresentOnPage(loc) ? findElementSuppressAlert(loc).isDisplayed() : false;
    }

    public boolean isElementLoaded(String name) {
        By loc = getElementLocatorByName(name,false,false);

        WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), TIME_OUT_IN_SECONDS);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(loc));
        return element.isEnabled();
    }

    public boolean isElementLoadedAndDisplayed(String name) {
        By loc = getElementLocatorByName(name,true,false);
        WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), TIME_OUT_IN_SECONDS);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(loc));

        return element.isDisplayed();
    }


    public boolean isAjaxJQueryCompleted() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        Boolean scriptResult = (Boolean) js.executeScript("return jQuery.active == 0");
        return scriptResult;
    }


    public boolean isHiddenElementLoaded(By name) {
        WebDriverWait wait = new WebDriverWait(getDriverProvider().get(), TIME_OUT_IN_SECONDS);
        WebElement present = wait.until(ExpectedConditions.presenceOfElementLocated(name));
        return present.isEnabled();
    }


    public int getElementsSize(By loc) {
        return findElements(loc).size();
    }

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
            Thread.sleep(WAIT_AFTER_HOVER_TIMEOUT);
        } catch (InterruptedException e) {
            Assert.assertTrue("", false);
        }
    }

    /**
     * <p/>
     * Method used to switch the focus to Alert Box
     */
    public boolean isAlertPresent() {
        try {
            switchTo().alert();
            return true;
        }   // try
        catch (Exception Ex) {
            return false;
        }   // catch
    }

    public void closeAlert() {
        switchTo().alert().accept();
    }

    /**
     * <p/>
     * Method used to get data from mini shopping bag
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
     * @param name
     */
    public String getHTMLSourceOfElement(String name) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        By loc = getElementLocatorByName(name);
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
            if (!block.getElementInfoByName(elName).isEmpty()) {
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

    //new
    public String getElementCssAttributeValue(String name, String attr) {
        String val = getElementByName(name).getCssValue(attr).toString();
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
            try {
                Thread.sleep(WAIT_ALERT_DISMISS);
            } catch (InterruptedException e1) {
                Assert.assertTrue("[ERROR] Exception in waiting for alert to disappear:" + e1.getMessage(), false);
            }
            return findElement(loc);
        }
    }

    public static WebElement getRandomWebElement(List<WebElement> elements) {
        int size = elements.size();
        Assert.assertTrue("[ERROR] Cannot select random element from empty list.", size > 0);
        Random randomGenerator = new Random();
        return elements.get(randomGenerator.nextInt(size));
    }

    public void applyPromoOnEnterPress() {
        Actions builder = new Actions(getDriver());
        builder.sendKeys(Keys.ENTER).perform();
        builder.sendKeys(Keys.ENTER).release();
    }

    public void switchToIframeWithLoc(By loc) {
        try {
            WebElement element = findElementSuppressAlert(loc);
            if (element.getAttribute("id").isEmpty()) {
                switchTo().frame(findElementSuppressAlert(loc));
            } else {
                switchTo().frame(findElementSuppressAlert(loc).getAttribute("id"));
            }
        } catch (NoSuchFrameException e) {
            Assert.assertTrue("[ERROR] Element with locator " + loc + " is not an iframe:" + e.getMessage(), false);
        }

    }

}
