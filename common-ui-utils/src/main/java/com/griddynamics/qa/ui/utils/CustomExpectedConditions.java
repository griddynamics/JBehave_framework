package com.griddynamics.qa.ui.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;

/**
 * Class with custom conditions for evaluation
 *
 * @author ybaturina
 */

public class CustomExpectedConditions {

    protected CustomExpectedConditions() {
    }

    /**
     * Wait until all scripts finished executing on the page
     *
     * @return true if no scripts are being executed, false otherwise
     */
    public static ExpectedCondition<Boolean> isDocumentReady() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
    }

    /**
     * Wait until an element is attached to the DOM.
     *
     * @param locator locator of an element to wait for.
     * @return true if the element is still attached to the DOM, false
     * otherwise.
     */
    public static ExpectedCondition<Boolean> isNotStaleElement(
            final By locator) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return findElement(locator, driver).isEnabled();
                } catch (StaleElementReferenceException expected) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return String.format("element with locator (%s) to become not stale", locator);
            }
        };
    }

    /**
     * An expectation for checking that an element is present on the DOM of a page
     * and visible.
     *
     * @param locator used to find the element
     * @return the WebElement once it is located and visible
     */
    public static ExpectedCondition<WebElement> visibilityOfElementLocated(
            final By locator) {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    WebElement element = findElement(locator, driver);
                    return element != null ? elementIfVisible(element) : null;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "visibility of element located by " + locator;
            }
        };
    }

    /**
     * An expectation for element with specified attribute value
     *
     * @param locator        locator of element
     * @param attribute      name of attribute to be checked
     * @param attributeValue expected value of attribute
     * @return true if attribute contains expected value, false otherwise
     */
    public static ExpectedCondition<Boolean> elementAttributeHasValue(
            final By locator, final String attribute, final String attributeValue) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                WebElement element = getVisibleElement(locator, driver);
                try {
                    return element.getAttribute(attribute) != null ?
                            element.getAttribute(attribute).contains(attributeValue) : false;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "element " + locator + " has attribute " + attribute + " with value " + attributeValue;
            }
        };
    }

    public static ExpectedCondition<Boolean> elementAttributeHasNoValue(
            final By locator, final String attribute, final String attributeValue) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                WebElement element = getVisibleElement(locator, driver);
                try {
                    return element.getAttribute(attribute) != null ?
                            !element.getAttribute(attribute).contains(attributeValue) : true;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "element " + locator + " has attribute " + attribute + " without value " + attributeValue;
            }
        };
    }

    /**
     * An expectation for element with specified css attribute value
     *
     * @param element
     * @param attribute      name of css attribute to be checked
     * @param attributeValue expected value of css attribute
     * @return true if attribute contains expected value, false otherwise
     */
    public static ExpectedCondition<Boolean> elementCssAttributeHasValue(
            final WebElement element, final String attribute, final String attributeValue) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return element.getCssValue(attribute) != null ?
                            element.getCssValue(attribute).contains(attributeValue) : false;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }
        };
    }

    public static ExpectedCondition<Boolean> elementCssAttributeHasNoValue(
            final WebElement element, final String attribute, final String attributeValue) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return element.getCssValue(attribute) == null ? true:
                            !element.getCssValue(attribute).contains(attributeValue);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }
        };
    }

    /**
     * @param locator locator of the element
     * @param count   count of the elements
     * @return true if count equals to expected, false otherwise
     */
    public static ExpectedCondition<Boolean> elementsHasCount(
            final By locator, final int count) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return findElementsSize(locator, driver) == count;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }
        };
    }

    /**
     * @param locator locator of the element
     * @param count   count of the elements
     * @return true if count not equals to expected, false otherwise
     */
    public static ExpectedCondition<Boolean> elementsHasNotCount(
            final By locator, final int count) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return findElementsSize(locator, driver) != count;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }
        };
    }

    /**
     * An expectation that an element has expected text
     *
     * @param locator locator of the element
     * @param text    expected text
     * @return true if element contains text, false otherwise
     */
    public static ExpectedCondition<Boolean> elementContainsText(
            final By locator, final String text) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                WebElement element = getVisibleElement(locator, driver);
                try {
                    String elementText = element.getText().replace("\n", " ").replace("\t", " ").replaceAll("<!--.*-->", "").trim();
                    return elementText.contains(text);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "element " + locator + " has text " + text;
            }
        };
    }

    /**
     * An expectation that an element has no expected text
     *
     * @param locator locator of the element
     * @param text    expected text
     * @return true if element does not contain text, false otherwise
     */
    public static ExpectedCondition<Boolean> elementContainsNoText(
            final By locator, final String text) {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                WebElement element = getVisibleElement(locator, driver);
                try {
                    return !element.getText().contains(text);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "element " + locator + " has no text " + text;
            }
        };
    }

    /**
     * An expectation for checking a locator is visible and enabled so that you
     * can click it.
     *
     * @param locator locator of the element
     * @return true if the element is clickable, false otherwise
     */
    public static ExpectedCondition<Boolean> locatorToBeClickable(
            final By locator) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                WebElement element = getVisibleElement(locator, driver);
                try {
                    return (element != null && element.isEnabled());
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "locator to be clickable: " + locator;
            }
        };
    }

    /**
     * An expectation for checking an element is visible and enabled so that you
     * can click it.
     *
     * @param element
     * @return true if the element is clickable, false otherwise
     */
    public static ExpectedCondition<Boolean> elementToBeClickable(
            final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return (element != null && element.isEnabled());
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }
        };
    }

    /**
     * An expectation for checking that an element is present on the DOM of a
     * page.
     *
     * @param locator used to find the element
     * @return the WebElement once it is located
     */
    public static ExpectedCondition<WebElement> presenceOfElementLocated(
            final By locator) {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return findElement(locator, driver);
            }

            @Override
            public String toString() {
                return "presence of element located by: " + locator;
            }
        };
    }

    /**
     * @return the given element if it is visible and has non-zero size, otherwise
     * null.
     */
    protected static WebElement elementIfVisible(WebElement element) {
        return element.isDisplayed() ? element : null;
    }

    protected static WebElement findElement(By by, WebDriver driver) {
        try {
            return findElementsSize(by, driver) > 0 ? driver.findElement(by) : null;
        } catch (WebDriverException e) {
            getLogger().warn(String.format("WebDriverException thrown by findElement(%s)", by), e);
            throw e;
        }
    }

    protected static int findElementsSize(By by, WebDriver driver) {
        try {
            return driver.findElements(by).size();
        } catch (WebDriverException e) {
            getLogger().warn(String.format("WebDriverException thrown by findElements(%s)", by), e);
            throw e;
        }
    }

    protected static WebElement getVisibleElement(By locator, WebDriver driver) {
        WebElement element = null;
        ExpectedCondition<WebElement> visibilityOfElementLocated =
                visibilityOfElementLocated(locator);
        try {
            element = visibilityOfElementLocated.apply(driver);
        } catch (TimeoutException e) {
            throw new RuntimeException("[ERROR] Locator " + locator + " is not visible");
        }
        return element;
    }

}
