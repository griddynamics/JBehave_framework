package com.griddynamics.testresult.model;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * @author lzakharova
 */
public class TestArea {
    private String areaId;
    private String areaName;
    private String fileName;
    private int width;
    private int height;
    private int posX;
    private int posY;
    private int browserWidth;
    private int browserHeight;
    private String browserName;
    private String browserVersion;

    private String croppedFromFileName;

    public TestArea(){}

    public TestArea(String pAreaId, String pAreaName, String pFileName) {
        areaId = pAreaId;
        areaName = pAreaName;
        fileName = pFileName;
    }

    public TestArea(String pAreaId, String pAreaName, String pFileName, int pWidth, int pHeight, int pPosX, int pPosY,
                    int pBrowserWidth, int pBrowserHeight, String pBrowserName, String pBrowserVersion) {
        areaId = pAreaId;
        areaName = pAreaName;
        fileName = pFileName;
        width = pWidth;
        height = pHeight;
        posX = pPosX;
        posY = pPosY;
        browserWidth = pBrowserWidth;
        browserHeight = pBrowserHeight;
        browserName = pBrowserName;
        browserVersion = pBrowserVersion;
    }

    @Override
    public String toString() {
        return "TestArea: areaId=" + areaId + "; areaName=" + areaName + "; fileName=" + fileName + ".";
    }



    public String getAreaId() {
        return areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
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

    public String getCroppedFromFileName() {
        return croppedFromFileName;
    }

    public void setCroppedFromFileName(String croppedFromFileName) {
        this.croppedFromFileName = croppedFromFileName;
    }

    public static CellProcessor[] getTestAreaProcessor() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), //areaId
                new NotNull(), //areaName
                new NotNull(), //fileName
                new ParseInt(),//width
                new ParseInt(),//height
                new ParseInt(),//posX
                new ParseInt(),//posY
                new ParseInt(),//browserWidth
                new ParseInt(),//browserHeight
                new NotNull(), //browserName
                new NotNull()  //browserVersion
        };

        return processors;
    }
}
