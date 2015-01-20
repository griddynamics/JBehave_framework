package com.griddynamics.testresult.dao;

import com.griddynamics.testresult.model.TestArea;

import java.util.List;

/**
 * @author lzakharova
 */
public interface TestAreaDAO {
    public void initTestAreaDB();

    public TestArea getTestArea(String areaId);

    public void addTestArea(TestArea testArea);

    public List<TestArea> getTestAreas();
}
