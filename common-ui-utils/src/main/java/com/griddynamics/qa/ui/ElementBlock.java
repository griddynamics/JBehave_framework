/*
 * Copyright 2015, Grid Dynamics International and/or its affiliates. All rights reserved.
 * Grid Dynamics International PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.griddynamics.qa.ui;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author ybaturina
 * @author mlykosova
 *         <p/>
 *         Base block class containing common block methods
 */
public class ElementBlock extends CommonElementMethods implements Cloneable {

    private final static int WAIT_BLOCK_LOAD_TIMEOUT_IN_MS = 500;
    private final static int WAIT_FOR_BLOCK_LOAD_TIMEOUT_IN_MS = 5000;

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
        return isLocatorPresentOnPage(locator) ? findElementSuppressAlert(locator).getText() : "";
    }

    /**
     * Returns block's HTML content as String
     */
    public String getBlockInnerHTML() {
        return isLocatorPresentOnPage(locator) ? findElementSuppressAlert(locator).getAttribute("innerHTML") : "";
    }

    public boolean isBlockDisplayed() {
        return isLocatorDisplayedOnPage(getLocator());
    }

    public boolean isBlockNotDisplayed() {
        return !isLocatorDisplayedOnPage(getLocator(), false, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    public boolean isBlockPresent() {
        return isLocatorPresentOnPage(getLocator());
    }

    public ElementBlock getBlockByIndex(int index) {
        return getBlockList().get(index);
    }

    /**
     * use isBlockDisplayed() instead
     */
    @Deprecated
    public void waitForBlockToLoad() {
        long currentSleepCount = 0;
        long totalSleepCount = WAIT_FOR_BLOCK_LOAD_TIMEOUT_IN_MS / WAIT_BLOCK_LOAD_TIMEOUT_IN_MS;
        while (!isBlockDisplayed() && currentSleepCount < totalSleepCount) {
            currentSleepCount++;
            try {
                Thread.sleep(WAIT_BLOCK_LOAD_TIMEOUT_IN_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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
        assertThat("[ERROR] Cannot select random block from empty list.", size, greaterThan(0));
        Random randomGenerator = new Random();
        return elementBlocks.get(randomGenerator.nextInt(size));
    }

    /**
     * Clear all blocks and all elements in current block
     */
    public void clear() {
        getElementsMap().clear();
        getBlockList().clear();
    }

    /**
     * Check that block not empty and does contain other blocks
     *
     * @param elementBlock
     * @return
     */
    public static boolean isEmptyBlock(ElementBlock elementBlock) {
        return (elementBlock == null || elementBlock.getBlockList().size() == 0) ? true: false;
    }

    public static boolean hasBlockWithName(List<ElementBlock> blockList, String blockName) {
        for (ElementBlock elementBlock : blockList) {
            if (elementBlock.getName().equals(blockName)){
                return true;
            }
        }
        return false;
    }

    public ElementBlock clone() {
        ElementBlock block = null;
        try {
            block = (ElementBlock) super.clone();

            for (Map.Entry<String, By> elementInfo : getElementsMap().entrySet()) {
                block.addElement(elementInfo.getKey(), elementInfo.getValue());
            }

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }

        return block;

    }
}
