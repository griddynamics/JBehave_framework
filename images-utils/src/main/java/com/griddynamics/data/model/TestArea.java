package com.griddynamics.data.model;

import org.supercsv.cellprocessor.ParseBool;
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
    private boolean countFromTop;

    private TestPage page = new TestPage();


    private String croppedFromFileName;

    public TestArea() {
    }

    public TestArea(String pAreaId, String pAreaName, String pFileName) {
        areaId = pAreaId;
        areaName = pAreaName;
        fileName = pFileName;
    }

    public TestArea(String pAreaId, String pAreaName, String pFileName, TestPage pPage,
                    int pWidth, int pHeight, int pPosX, int pPosY, boolean pCountFromTop) {
        areaId = pAreaId;
        areaName = pAreaName;
        fileName = pFileName;
        page = pPage;
        width = pWidth;
        height = pHeight;
        posX = pPosX;
        posY = pPosY;
        countFromTop = pCountFromTop;
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

    public TestPage getPage() {
        return page;
    }

    public void setPageName(TestPage page) {
        this.page = page;
    }

    public void setPageId(String pageId) {
        this.page = new TestPage();
        this.page.setPageID(pageId);
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

    public boolean getCountFromTop() {
        return countFromTop;
    }

    public void setCountFromTop(boolean countFromTop) {
        this.countFromTop = countFromTop;
    }

    public String getCroppedFromFileName() {
        return croppedFromFileName;
    }

    public void setCroppedFromFileName(String croppedFromFileName) {
        this.croppedFromFileName = croppedFromFileName;
    }

    public static CellProcessor[] getTestAreaProcessor() {

        final CellProcessor[] processors = new CellProcessor[]{
                new NotNull(), //areaId
                new NotNull(), //areaName
                new NotNull(), //fileName
                new NotNull(), //pageId
                new ParseInt(),//width
                new ParseInt(),//height
                new ParseInt(),//posX
                new ParseInt(),//posY
                new ParseBool()//countFromTop
        };

        return processors;
    }
}
