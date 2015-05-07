package com.griddynamics.data.model;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * @author lzakharova
 */
public class TestPage {
    private String pageID;
    private String pageName;
    private String fileName;

    private String browserName;
    private String browserVersion;
    private int browserWidth;
    private int browserHeight;
    private int imageHeight;

    public TestPage() {
    }

    public TestPage(String pPageID, String pPageName, String pFileName,
                    String pBrowserName, String pBrowserVersion, int pBrowserWidth, int pBrowserHeight, int pImageHeight) {
        pageID = pPageID;
        pageName = pPageName;
        fileName = pFileName;
        browserName = pBrowserName;
        browserVersion = pBrowserVersion;
        browserWidth = pBrowserWidth;
        browserHeight = pBrowserHeight;
        imageHeight = pImageHeight;
    }

    @Override
    public String toString() {
        return "TestPage: pageID=" + pageID + "; pageName=" + pageName + "; fileName=" + fileName + ".";
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public int getBrowserWidth() {
        return browserWidth;
    }

    public void setBrowserWidth(int browserWidth) {
        this.browserWidth = browserWidth;
    }

    public int getBrowserHeight() {
        return browserHeight;
    }

    public void setBrowserHeight(int browserHeight) {
        this.browserHeight = browserHeight;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public static CellProcessor[] getTestPageProcessor() {

        final CellProcessor[] processors = new CellProcessor[]{
                new NotNull(), //pageID
                new NotNull(), //pageName
                new NotNull(), //fileName
                new NotNull(), //browserName
                new NotNull(), //browserVersion
                new ParseInt(),//browserWidth
                new ParseInt(),//browserHeight
                new ParseInt() //imageHeight
        };

        return processors;
    }
}
