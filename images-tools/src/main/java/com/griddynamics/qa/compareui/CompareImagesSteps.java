package com.griddynamics.qa.compareui;

import com.griddynamics.qa.ui.Pages;
import com.griddynamics.testresult.dao.TestAreaDAO;
import com.griddynamics.testresult.dao.TestResultDAO;
import com.griddynamics.testresult.model.TestArea;
import com.griddynamics.testresult.model.TestResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.jbehave.core.annotations.Then;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

@Component
@Scope("thread")
public class CompareImagesSteps {


    @Autowired
    public Pages pages;

    @Autowired
    public TestAreaDAO testAreaDAO;

    @Autowired
    public TestResultDAO testResultDAO;

    @Value( "${full.page.screenshot.file.name}" )
    private String fullPageScreenshotFileName;

    @Value( "${cropped.image.file.name}" )
    private String croppedScreenshotFileName;

    @Value( "${comparison.image.file.name}" )
    private String comparisonResultFileName;

    @Value( "${testimages.data.root.folder.path}" )
    private String rootFolderPath;

    @Value( "${baseline.image.folder.name}" )
    private String baselineImageFolderName;

    @Value( "${result.image.folder.name}" )
    private String resultImageFolderName;

    @Value( "${test.areas.image.folder.name}" )
    private String testAreasImagesFolderName;

    @Value( "${imagemagic.home}" )
    private String imageMagicHomeFolder;

    @Value( "${browser.width}" )
    private int browserWidthP;

    @Value( "${browser.height}" )
    private int browserHeightP;


    public static final String TEST_SESSION_TS = getTimestamp();

    public static final String FUZZ_VALUE = "50%";
    public static final String CORRECT_RESPONSE_CONTENT = "Channel distortion: AE";

    public static final String CROP_COMMAND =  "convert -verbose -crop %sx%s+%s+%s %s %s";
    public static final String COMPARE_COMMAND = "compare -fuzz %s -metric AE -verbose %s %s %s";


    @Then("customer checks UI for $uiAreaName")
    public void checkUI(String uiAreaName) throws IOException, InterruptedException {
        TestArea uiObject = getTestObjectForArea(testAreaDAO.getTestAreas(), uiAreaName,
                getBrowserName(), getBrowserVersion(), browserWidthP, browserHeightP);


        setBrowserSize(uiObject.getBrowserWidth(), uiObject.getBrowserHeight());

        String testTs = getTimestamp();

        File fullPageScreenshot = ((TakesScreenshot)pages.getCurrentPage().getDriver()).getScreenshotAs(OutputType.FILE);


        String testFolderName = RandomStringUtils.randomAlphabetic(32).toLowerCase();
        String resultsDirectoryName = getFullPath(rootFolderPath, resultImageFolderName) +
                testTs.replaceAll("[\\. :-]", "").substring(0,12) + File.separator + testFolderName + File.separator;
        (new File(resultsDirectoryName)).mkdir();

        File fullScreenshotFinal = new File(resultsDirectoryName + fullPageScreenshotFileName);
        FileUtils.copyFile(fullPageScreenshot, fullScreenshotFinal);
        fullPageScreenshot.delete();

        int y = uiObject.getPosY();
        if (uiObject.getPosY() == 9999) {
            BufferedImage bimg = ImageIO.read(fullScreenshotFinal);
            y = bimg.getHeight() - uiObject.getHeight();
        }

        // Crop image
        String result = executeCommand(
                String.format(addPathSeparatorToTheEnd(imageMagicHomeFolder) + CROP_COMMAND,
                uiObject.getWidth(), uiObject.getHeight(), uiObject.getPosX(), y,
                resultsDirectoryName + fullPageScreenshotFileName,
                resultsDirectoryName + croppedScreenshotFileName));
        getLogger().info(result);

        // Compare cropped image to baseline
        result = executeCommand(
                String.format(addPathSeparatorToTheEnd(imageMagicHomeFolder) + COMPARE_COMMAND,
                FUZZ_VALUE,
                getFullPath(rootFolderPath, baselineImageFolderName) + uiObject.getFileName(),
                resultsDirectoryName + croppedScreenshotFileName,
                resultsDirectoryName + comparisonResultFileName));
        getLogger().info(result);

        getLogger().info("SCREENSHOT<cropped image> " + resultsDirectoryName + croppedScreenshotFileName);
        getLogger().info("SCREENSHOT<baseline image> " + getFullPath(rootFolderPath, baselineImageFolderName) + uiObject.getFileName());
        getLogger().info("SCREENSHOT<result image> " + resultsDirectoryName + comparisonResultFileName);

        // Check that compare command run contains expected response
        if (!result.contains(CORRECT_RESPONSE_CONTENT)) {
            fail("Wrong output from comparison command:\n" + result);
        }

        // Get number of pixels which differ
        result = getMatchString(result, "all: (\\d+)");
        result = getMatchString(result, "\\d+");
        int numberOfDiffPixels = Integer.parseInt(result);

        // Save test result
        TestResult tr = new TestResult(null, uiObject.getAreaId(), TEST_SESSION_TS, testTs,
                (numberOfDiffPixels == 0) ? true : false, numberOfDiffPixels, false, testFolderName);
        testResultDAO.importTestResult(tr);

        // Check test result
        assertTrue("Original and actual ui areas differs. Number of different pixels: " + numberOfDiffPixels,
                numberOfDiffPixels == 0);
    }



    private static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);

            BufferedReader stReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line;
            while ((line = stReader.readLine())!= null) {
                output.append(line + "\n");
            }

            while ((line = errReader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return output.toString();
    }


    public static String getTimestamp() {
        return (new Timestamp((new java.util.Date()).getTime())).toString();
    }

    private void setBrowserSize(int width, int height) {
        pages.getCurrentPage().getDriver().manage().window().setSize(new Dimension(width, height));
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


    private static List<TestArea> getTestObjectsForArea(List<TestArea> objects, String pAreaName) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for(TestArea obj: objects) {
            if (obj.getAreaName().equals(pAreaName)) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowser(List<TestArea> objects, String browser) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for(TestArea obj: objects) {
            if (obj.getBrowserName().equals(browser)) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowserVersion(List<TestArea> objects, String browserVersion) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for(TestArea obj: objects) {
            if (obj.getBrowserVersion().equals(browserVersion)) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowserWidth(List<TestArea> objects, int browserWidth) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for(TestArea obj: objects) {
            if (obj.getBrowserWidth() == browserWidth) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }

    private static List<TestArea> getTestObjectsForBrowserHeight(List<TestArea> objects, int browserHeight) {
        List<TestArea> testAreas = new LinkedList<TestArea>();
        for(TestArea obj: objects) {
            if (obj.getBrowserHeight() == browserHeight) {
                testAreas.add(obj);
            }
        }
        return testAreas;
    }


    private static String getFullPath(String path, String folderName) {
        if (!path.endsWith(File.separator) && !folderName.startsWith(File.separator)) {
            return path + File.separator + folderName + File.separator;
        } else {
            return path + folderName + File.separator;
        }
    }


    private static String addPathSeparatorToTheEnd(String path) {
        if (!path.endsWith(File.separator)) {
            return path + File.separator;
        }
        return path;
    }

    private String getBrowserName() {
        String name = ((RemoteWebDriver)pages.getCurrentPage().getDriver()).getCapabilities().getBrowserName();
        getLogger().info("Browser name = " + name);
        return name;
    }

    private String getBrowserVersion() {
        String version = ((RemoteWebDriver)pages.getCurrentPage().getDriver()).getCapabilities().getVersion();
        getLogger().info("Browser full version = " + version);
        version = getMatchString(version, "\\d+");
        getLogger().info("Browser major version = " + version);
        return version;
    }
}
