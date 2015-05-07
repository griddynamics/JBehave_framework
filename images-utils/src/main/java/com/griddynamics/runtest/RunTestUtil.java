package com.griddynamics.runtest;

import com.griddynamics.data.dao.TestAreaDAO;
import com.griddynamics.data.dao.TestResultDAO;
import com.griddynamics.data.model.FileStructure;
import com.griddynamics.data.model.TestArea;
import com.griddynamics.data.model.TestResult;
import com.griddynamics.imwrapper.ImageMagickWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import static com.griddynamics.qa.logger.LoggerFactory.getLogger;
import static com.griddynamics.qa.tools.StringParser.getMatchString;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * @author lzakharova
 */
public class RunTestUtil {

    @Autowired
    public TestAreaDAO testAreaDAO;

    @Autowired
    public TestResultDAO testResultDAO;

    @Autowired
    public ImageMagickWrapper imageMagickWrapper;


    private static final String TEST_SESSION_TS = getTimestamp();


    public void runUITestForPage(WebDriver driver, String pageName,
                                 int browserHeightInPix, int browserWidthInPix,
                                 FileStructure fileStructure) {
        List<TestArea> testObjects = getTestObjectsForPage(testAreaDAO.getTestAreas(), pageName,
                getBrowserName(driver), getBrowserVersion(driver), browserWidthInPix, browserHeightInPix);
        for (TestArea uiObject : testObjects) {
            runUITest(driver, uiObject, fileStructure);
        }
    }


    public void runUITestForArea(WebDriver driver, String uiAreaName,
                                 int browserHeightInPix, int browserWidthInPix,
                                 FileStructure fileStructure) {
        TestArea uiObject = getTestObjectForArea(testAreaDAO.getTestAreas(), uiAreaName,
                getBrowserName(driver), getBrowserVersion(driver), browserWidthInPix, browserHeightInPix);
        runUITest(driver, uiObject, fileStructure);
    }

    public void runUITest(WebDriver driver, TestArea uiObject, FileStructure fileStructure) {


        setBrowserSize(driver, uiObject.getPage().getBrowserWidth(), uiObject.getPage().getBrowserHeight());

        String testTs = getTimestamp();

        String testFolderName = RandomStringUtils.randomAlphabetic(32).toLowerCase();
        String resultsDirectoryName = fileStructure.getResultImagePath() +
                testTs.replaceAll("[\\. :-]", "").substring(0, 12) + File.separator + testFolderName + File.separator;
        (new File(resultsDirectoryName)).mkdir();

        File fullScreenshotFinal = takeScreenshot(driver, resultsDirectoryName + fileStructure.getFullPageScreenshotFileName());


        // Case when y position should be counted from the bottom
        int y = uiObject.getPosY();
        if (!uiObject.getCountFromTop()) {
            BufferedImage bimg = null;
            try {
                bimg = ImageIO.read(fullScreenshotFinal);
            } catch (IOException ex) {
                fail("Can't read image " + fullScreenshotFinal + ".\n" + ex.getMessage());
            }
            y = bimg.getHeight() - uiObject.getHeight() - y;
        }


        // Crop image
        imageMagickWrapper.crop(
                resultsDirectoryName + fileStructure.getFullPageScreenshotFileName(),
                resultsDirectoryName + fileStructure.getCroppedScreenshotFileName(),
                uiObject.getPosX(),
                y,
                uiObject.getWidth(),
                uiObject.getHeight());


        // Compare cropped image to the baseline
        int numberOfDiffPixels = imageMagickWrapper.compare(
                fileStructure.getBaselineImagePath() + uiObject.getFileName(),
                resultsDirectoryName + fileStructure.getCroppedScreenshotFileName(),
                resultsDirectoryName + fileStructure.getComparisonResultFileName()
        );

        getLogger().info("SCREENSHOT<cropped image> " + resultsDirectoryName + fileStructure.getCroppedScreenshotFileName());
        getLogger().info("SCREENSHOT<baseline image> " + fileStructure.getBaselineImagePath() + uiObject.getFileName());
        getLogger().info("SCREENSHOT<result image> " + resultsDirectoryName + fileStructure.getComparisonResultFileName());


        // Save test result
        TestResult tr = new TestResult(null, uiObject.getAreaId(), TEST_SESSION_TS, testTs,
                (numberOfDiffPixels == 0) ? true : false, numberOfDiffPixels, false, testFolderName);
        testResultDAO.importTestResult(tr);

        // Check test result
        assertTrue("Original and actual ui areas differs. Number of different pixels: " + numberOfDiffPixels,
                numberOfDiffPixels == 0);
    }


    public File takeScreenshot(int browserHeightInPix, int browserWidthInPix, WebDriver driver, String saveToFullPath) {
        setBrowserSize(driver, browserWidthInPix, browserHeightInPix);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return takeScreenshot(driver, saveToFullPath);
    }

    public File takeScreenshot(WebDriver driver, String saveToFullPath) {

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File fullScreenshotFinal = new File(saveToFullPath);

        try {
            FileUtils.copyFile(screenshot, fullScreenshotFinal);
        } catch (IOException ex) {
            fail("Can't copy file from " + screenshot.getAbsolutePath() + " to " + saveToFullPath + ".\n" + ex.getMessage());
        }

        screenshot.delete();

        return fullScreenshotFinal;
    }


    public static String getTimestamp() {
        return (new Timestamp((new java.util.Date()).getTime())).toString();
    }

    private void setBrowserSize(WebDriver driver, int width, int height) {
        driver.manage().window().setSize(new Dimension(width + 15, height));
    }


    private static List<TestArea> getTestObjectsForPage(List<TestArea> objects, String pPageName,
                                                        String browser, String browserVersion,
                                                        int browserWidth, int browserHeight) {
        List<TestArea> testAreas;

        testAreas = getTestObjectsForPageName(objects, pPageName);
        assertTrue("There is no test object with name " + pPageName, testAreas.size() > 0);

        testAreas = getTestObjectsForBrowser(testAreas, browser);
        testAreas = getTestObjectsForBrowserVersion(testAreas, browserVersion);
        testAreas = getTestObjectsForBrowserWidth(testAreas, browserWidth);
        testAreas = getTestObjectsForBrowserHeight(testAreas, browserHeight);

        assertTrue("There should be at least one test area with same page name, browser, browser version," +
                "browser size. There are " + testAreas.size() + " now.", testAreas.size() >= 1);

        return testAreas;
    }


    private static TestArea getTestObjectForArea(List<TestArea> objects, String pAreaName,
                                                 String browser, String browserVersion,
                                                 int browserWidth, int browserHeight) {
        List<TestArea> testAreas;

        testAreas = getTestObjectsForArea(objects, pAreaName);
        assertTrue("There is no test object with name " + pAreaName, testAreas.size() > 0);

        testAreas = getTestObjectsForBrowser(testAreas, browser);
        assertTrue("There is no test object with name " + pAreaName + " for browser " + browser, testAreas.size() > 0);

        testAreas = getTestObjectsForBrowserVersion(testAreas, browserVersion);
        assertTrue("There is no test object with name " + pAreaName + " for browser " + browser +
                " version " + browserVersion, testAreas.size() > 0);

        testAreas = getTestObjectsForBrowserWidth(testAreas, browserWidth);
        assertTrue("There is no test object with name " + pAreaName + " for browser " + browser +
                " version " + browserVersion + ", width=" + browserWidth, testAreas.size() > 0);

        testAreas = getTestObjectsForBrowserHeight(testAreas, browserHeight);
        assertTrue("There is no test object with name " + pAreaName + " for browser " + browser +
                " version " + browserVersion + ", width=" + browserWidth + ", height=" + browserHeight,
                testAreas.size() > 0);

        assertTrue("There should be only one test area with sane area name, browser, browser version," +
                "browser size. There are " + testAreas.size() + " now.", testAreas.size() == 1);

        return testAreas.get(0);
    }


    private static List<TestArea> getTestObjectsForPageName(List<TestArea> objects, String pPageName) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for (TestArea obj : objects) {
            if (obj.getPage().getPageName().equals(pPageName)) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }


    private static List<TestArea> getTestObjectsForArea(List<TestArea> objects, String pAreaName) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for (TestArea obj : objects) {
            if (obj.getAreaName().equals(pAreaName)) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowser(List<TestArea> objects, String browser) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for (TestArea obj : objects) {
            if (obj.getPage().getBrowserName().equals(browser)) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowserVersion(List<TestArea> objects, String browserVersion) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for (TestArea obj : objects) {
            if (obj.getPage().getBrowserVersion().equals(browserVersion)) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowserWidth(List<TestArea> objects, int browserWidth) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for (TestArea obj : objects) {
            if (obj.getPage().getBrowserWidth() == browserWidth) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowserHeight(List<TestArea> objects, int browserHeight) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for (TestArea obj : objects) {
            if (obj.getPage().getBrowserHeight() == browserHeight) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }


    private String getBrowserName(WebDriver driver) {
        String name = ((RemoteWebDriver) driver).getCapabilities().getBrowserName();
        getLogger().info("Browser name = " + name);
        return name;
    }

    private String getBrowserVersion(WebDriver driver) {
        String version = ((RemoteWebDriver) driver).getCapabilities().getVersion();
        getLogger().info("Browser full version = " + version);
        version = getMatchString(version, "\\d+");
        getLogger().info("Browser major version = " + version);
        return version;
    }


}
