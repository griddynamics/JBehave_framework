package com.griddynamics.data.dao;

import com.griddynamics.data.model.TestArea;
import com.griddynamics.data.model.TestPage;

import java.util.List;
import java.util.Map;

/**
 * @author lzakharova
 */
public interface TestAreaDAO {
    public void initTestAreaDB();

    public TestArea getTestArea(String areaId);

    public void deleteTestArea(String areaId);

    public void addTestArea(TestArea testArea);

    public void updateTestArea(TestArea testArea);

    public List<TestArea> getTestAreas();

    public List<TestArea> getTestAreasForPage(String pageId);

    public Map<String, List<TestArea>> getTestAreasGroupedByPage();
}
