package com.griddynamics.data.model;

import java.io.File;

/**
 * @author lzakharova
 */
public class FileStructure {


    private String rootFolderPath;

    private String resultImageFolderName;
    private String baselineImageFolderName;
    private String testPagesFolderName;

    private String fullPageScreenshotFileName;
    private String croppedScreenshotFileName;
    private String comparisonResultFileName;


    private String baselineImagePath = null;
    private String resultImagePath = null;
    private String testAreasImagesPath = null;
    private String testPagesPath = null;



    private FileStructure (){}


    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public String getResultImageFolderName() {
        return resultImageFolderName;
    }

    public void setResultImageFolderName(String resultImageFolderName) {
        this.resultImageFolderName = resultImageFolderName;
    }

    public String getBaselineImageFolderName() {
        return baselineImageFolderName;
    }

    public void setBaselineImageFolderName(String baselineImageFolderName) {
        this.baselineImageFolderName = baselineImageFolderName;
    }

    public String getTestPagesFolderName() {
        return testPagesFolderName;
    }

    public void setTestPagesFolderName(String testPagesFolderName) {
        this.testPagesFolderName = testPagesFolderName;
    }

    public String getFullPageScreenshotFileName() {
        return fullPageScreenshotFileName;
    }

    public void setFullPageScreenshotFileName(String fullPageScreenshotFileName) {
        this.fullPageScreenshotFileName = fullPageScreenshotFileName;
    }

    public String getCroppedScreenshotFileName() {
        return croppedScreenshotFileName;
    }

    public void setCroppedScreenshotFileName(String croppedScreenshotFileName) {
        this.croppedScreenshotFileName = croppedScreenshotFileName;
    }

    public String getComparisonResultFileName() {
        return comparisonResultFileName;
    }

    public void setComparisonResultFileName(String comparisonResultFileName) {
        this.comparisonResultFileName = comparisonResultFileName;
    }

    public String getBaselineImagePath() {
        baselineImagePath = (baselineImagePath == null) ?
                (rootFolderPath + (rootFolderPath.endsWith(File.separator) ? "" : File.separator) +
                        baselineImageFolderName + File.separator)
                : baselineImagePath;
        return baselineImagePath;
    }

    public String getResultImagePath() {
        resultImagePath = (resultImagePath == null) ?
                (rootFolderPath + (rootFolderPath.endsWith(File.separator) ? "" : File.separator) +
                        resultImageFolderName + File.separator)
                : resultImagePath;
        return resultImagePath;
    }

    public String getTestPagesPath() {
        testPagesPath = (testPagesPath == null) ?
                (rootFolderPath + (rootFolderPath.endsWith(File.separator) ? "" : File.separator) +
                        testPagesFolderName + File.separator)
                : testPagesPath;
        return testPagesPath;
    }
}
