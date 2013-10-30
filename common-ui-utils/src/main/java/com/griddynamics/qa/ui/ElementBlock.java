package com.griddynamics.qa.ui;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static org.junit.Assert.assertTrue;

/**
 * @author ybaturina
 *         mlykosova
 *         <p/>
 *         Base block class containing common block methods
 */
public class ElementBlock extends CommonElementMethods implements Cloneable{

    private final static int WAIT_BLOCK_LOAD_TIMEOUT_IN_MS = 1000;

    private String blockName;
    private By locator;

    public ElementBlock(WebDriverProvider provider, String blockName, By locator) {
        super(provider);

        this.blockName = blockName;
        this.locator = locator;
    }

    public ElementBlock(WebDriverProvider provider, String blockName) {
        super(provider);
        this.blockName = blockName;
    }

    public ElementBlock(WebDriverProvider provider) {
        super(provider);
    }

    public String getName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public void setLocator(By loc) {
        this.locator = loc;
    }

    public By getLocator() {
        return locator;
    }

    public String getBlockText() {
        return isLocatorPresentOnPage(locator) ? findElement(locator).getText() : "";
    }

    /**
     * Returns block's HTML content as String
     */
    public String getBlockInnerHTML() {
        return isLocatorPresentOnPage(locator) ? findElement(locator).getAttribute("innerHTML") : "";
    }

    public boolean isBlockDisplayed() {
        return isLocatorDisplayedOnPage(getLocator());
    }

    public boolean isBlockPresent() {
        return isLocatorPresentOnPage(getLocator());
    }

    public ElementBlock getBlockByIndex(int index) {
        return getBlockList().get(index);
    }

    public void waitForBlockToLoad() {
        long second = 0;
        while (!isBlockDisplayed() && second < 10 * WAIT_BLOCK_LOAD_TIMEOUT_IN_MS) {
            second = second + WAIT_BLOCK_LOAD_TIMEOUT_IN_MS;
            try {
                Thread.sleep(WAIT_BLOCK_LOAD_TIMEOUT_IN_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException("[ERROR] Block " + getName() + " was not loaded, reason: " + e.getMessage());
            }
        }
    }


    public void assertBlockDisplayed() {
        assertTrue("[ERROR] Block \"" + getName() + "\" with locator: " + getLocator() +
                " is not displayed on the page", isBlockDisplayed());
    }

    public void assertBlockPresent() {
        assertTrue("[ERROR] Block \"" + getName() + "\" with locator: " + getLocator() +
                " is not present on the page", isBlockPresent());
    }

    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof ElementBlock)) {
	    return false;
	}
	ElementBlock other = (ElementBlock) obj;
	return this.getName().equals(other.getName()) && this.getLocator().equals(other.getLocator());
    }

    public static ElementBlock getRandomElementBlock(List<ElementBlock> elementBlocks) {
        int size = elementBlocks.size();
        assertTrue("[ERROR] Cannot select random block from empty list.", size > 0);
        Random randomGenerator = new Random();
        return elementBlocks.get(randomGenerator.nextInt(size));
    }

    public void clear(){
        getElementsMap().clear();
        getBlockList().clear();
    }

    public static boolean isEmptyBlock(ElementBlock elementBlock) {
        if (elementBlock == null || elementBlock.getBlockList().size() == 0)  {
            return true;
        }
        return false;
    }

    public static boolean hasBlockWithName(List<ElementBlock> blockList, String blockName) {
        boolean result = false;
        for (ElementBlock elementBlock : blockList) {
            if (elementBlock.getName().contains(blockName))
            result = true;
        }
        return result;
    }

    public ElementBlock clone() {
        ElementBlock block = null;
        try {
            block = (ElementBlock) super.clone();

            for (Map.Entry<String, By> elementInfo: getElementsMap().entrySet()) {
                block.addElement(elementInfo.getKey(), elementInfo.getValue());
            }

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }

        return block;

    }
}
