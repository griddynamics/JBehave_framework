package com.griddynamics.data.model;

import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.Date;

/**
 * @author lzakharova
 */
public class TestResult implements Comparable<TestResult> {
    private int testId;
    private String testAreaId;
    private String testSessionTs;
    private String testRunTs;
    private boolean testResult;
    private int numberOfDiffPixels;
    private boolean isVerified;
    private String testFolderName;

    private TestArea testArea;

    public TestResult() {
    }

    public TestResult(Integer pTestId, String pTestAreaId, String pTestSessionTs, String pTestRunTs,
                      boolean pTestResult, int pNumberOfDiffPixels, boolean pIsVerified, String pTestFolderName) {
        testAreaId = pTestAreaId;
        testSessionTs = pTestSessionTs;
        testRunTs = pTestRunTs;
        testResult = pTestResult;
        numberOfDiffPixels = pNumberOfDiffPixels;
        testId = (pTestId == null) ? -1 : pTestId;
        isVerified = pIsVerified;
        testFolderName = pTestFolderName;
    }

    @Override
    public String toString() {
        return "TestResult: testId=" + testId + "; testAreaId=" + testAreaId + "; testSessionTs=" + testSessionTs +
                "; testRunTs=" + testRunTs + "; testResult=" + testResult +
                "; numberOfDiffPixels=" + numberOfDiffPixels + "; isVerified=" + isVerified +
                "; testFolderName" + testFolderName + ".";
    }

    public String getTestAreaId() {
        return testAreaId;
    }

    public String getTestSessionTs() {
        return testSessionTs;
    }

    public String getTestRunTs() {
        return testRunTs;
    }

    public boolean getTestResult() {
        return testResult;
    }

    public int getNumberOfDiffPixels() {
        return numberOfDiffPixels;
    }

    public int getTestId() {
        return testId;
    }

    public TestArea getTestArea() {
        return testArea;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public String getTestFolderName() {
        return testFolderName;
    }

    public void setTestArea(TestArea pTestArea) {
        testArea = pTestArea;
    }

    public String getResultFolderName() {
        return testRunTs.replaceAll("[\\. :-]", "").substring(0, 12);
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public void setTestAreaId(String testAreaId) {
        this.testAreaId = testAreaId;
    }

    public void setTestSessionTs(String testSessionTs) {
        this.testSessionTs = testSessionTs;
    }

    public void setTestRunTs(String testRunTs) {
        this.testRunTs = testRunTs;
    }

    public void setTestResult(boolean testResult) {
        this.testResult = testResult;
    }

    public void setNumberOfDiffPixels(int numberOfDiffPixels) {
        this.numberOfDiffPixels = numberOfDiffPixels;
    }

    public void setIsVerified(boolean verified) {
        isVerified = verified;
    }

    public void setTestFolderName(String testFolderName) {
        this.testFolderName = testFolderName;
    }


    @Override
    public int compareTo(TestResult that) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == that) return EQUAL;

        Long thisTimestamp = Long.parseLong(this.testRunTs.replaceAll("[\\. :-]", "").substring(0, 12));
        Long thatTimestamp = Long.parseLong(that.testRunTs.replaceAll("[\\. :-]", "").substring(0, 12));

        if (thisTimestamp < thatTimestamp) return BEFORE;
        if (thisTimestamp > thatTimestamp) return AFTER;

        return EQUAL;
    }


    public static CellProcessor[] getTestResultProcessor() {

        final CellProcessor[] processors = new CellProcessor[]{
                new ParseInt(), //testId
                new NotNull(),  //testAreaId
                new NotNull(),  //testSessionTs
                new NotNull(),  //testRunTs
                new ParseBool(),//testResult
                new ParseInt(), //numberOfDiffPixels
                new ParseBool(),//isVerified
                new NotNull(),  //testFolderName
        };

        return processors;
    }
}
