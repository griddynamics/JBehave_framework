package com.griddynamics.qa.compareui;

import com.griddynamics.data.model.FileStructure;
import com.griddynamics.imwrapper.ImageMagickWrapper;
import com.griddynamics.qa.ui.Pages;
import com.griddynamics.runtest.RunTestUtil;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.griddynamics.runtest.RunTestUtil.getTimestamp;


/**
 * @author lzakharova
 */

@Component
@Scope("thread")
public class CompareImagesSteps {


    @Autowired
    public Pages pages;


    @Autowired
    public RunTestUtil testRun;


    @Autowired
    public FileStructure fileStructure;

    @Autowired
    public ImageMagickWrapper imageMagickWrapper;


    @Value("${browser.width}")
    private int browserWidthP;

    @Value("${browser.height}")
    private int browserHeightP;


    @Then(value = "customer checks UI for $pageName Page", priority = 2)
    public void checkUIForPage(String pageName) {

        testRun.runUITestForPage(pages.getCurrentPage().getDriver(), pageName, browserHeightP, browserWidthP, fileStructure);
    }


    @Then("customer checks UI for $uiAreaName")
    public void checkUI(String uiAreaName) {

        testRun.runUITestForArea(pages.getCurrentPage().getDriver(), uiAreaName, browserHeightP, browserWidthP, fileStructure);
    }


    @Given("current page screenshot")
    public void takeScreenshot() {
        testRun.takeScreenshot(
                browserHeightP, browserWidthP,
                pages.getCurrentPage().getDriver(),
                (fileStructure.getRootFolderPath().endsWith(File.separator) ?
                        fileStructure.getRootFolderPath() : fileStructure.getRootFolderPath() + File.separator)
                        + "screenshot_" + getTimestamp() + ".png");
    }

}
