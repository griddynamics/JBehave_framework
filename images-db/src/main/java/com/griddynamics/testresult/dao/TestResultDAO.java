package com.griddynamics.testresult.dao;

import com.griddynamics.testresult.model.TestResult;

import java.util.List;

/**
 * @author lzakharova
 */
public interface TestResultDAO {
    public void initTestResultsDB();

    public void importTestResult(TestResult testResult);

    public TestResult getTestResult(int id);

    public List<TestResult> getAllTestResults();

    public List<TestResult> getTestResultsOnePerSession();

    public List<TestResult> getSessionResults(String sessionTS);

    public void setVerified(int testId, boolean isVerified);

    public void setTestResult(int testId, boolean isTestPassed);
}
