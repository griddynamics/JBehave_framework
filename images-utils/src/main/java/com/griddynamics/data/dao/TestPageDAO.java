package com.griddynamics.data.dao;

import com.griddynamics.data.model.TestPage;

import java.util.List;
import java.util.Map;

/**
 * @author lzakharova
 */
public interface TestPageDAO {
    public void initTestPageDB();

    public TestPage getTestPage(String pageId);

    public void deleteTestPage(String pageId);

    public void addTestPage(TestPage testPage);

    public Map<String, List<TestPage>> getTestPagesGroupedByPageName();

    public List<TestPage> getTestPages();
}
