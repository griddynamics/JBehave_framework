package com.griddynamics.data.dao.impl;

import com.griddynamics.data.dao.TestPageDAO;
import com.griddynamics.data.model.TestArea;
import com.griddynamics.data.model.TestPage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author lzakharova
 */
public class JdbcTestPageDAO implements TestPageDAO {

    public static final String TEST_PAGES_TABLE_NAME = "test_pages";
    public static final String PAGE_ID_DB_COL = "page_id";
    public static final String PAGE_NAME_DB_COL = "page_name";
    public static final String PAGE_FILE_NAME_DB_COL = "file_name";
    public static final String BROWSER_WIDTH_DB_COL = "browser_width";
    public static final String BROWSER_HEIGHT_DB_COL = "browser_height";
    public static final String BROWSER_NAME_DB_COL = "browser_name";
    public static final String BROWSER_VERSION_DB_COL = "browser_version";
    public static final String IMAGE_HEIGHT_DB_COL = "image_height";


    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void initTestPageDB() {
        String ifTableExists = "SELECT count(*) FROM sqlite_master " +
                "WHERE type='table' AND name='" + TEST_PAGES_TABLE_NAME + "'";

        String dropTable = "drop table " + TEST_PAGES_TABLE_NAME;


        String createTableSql = "CREATE TABLE " + TEST_PAGES_TABLE_NAME + "(" +
                PAGE_ID_DB_COL + " CHAR(128) NOT NULL, " +
                PAGE_NAME_DB_COL + " CHAR(128) NOT NULL, " +
                PAGE_FILE_NAME_DB_COL + " CHAR(128) NOT NULL, " +
                BROWSER_WIDTH_DB_COL + " INT, " +
                BROWSER_HEIGHT_DB_COL + " INT, " +
                BROWSER_NAME_DB_COL + " CHAR(16), " +
                BROWSER_VERSION_DB_COL + " CHAR(8)," +
                IMAGE_HEIGHT_DB_COL + " INT " + ")";

        Connection conn = null;
        PreparedStatement ps;

        try {
            conn = dataSource.getConnection();

            ps = conn.prepareStatement(ifTableExists);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                //table exists
                ps.close();
                ps = conn.prepareStatement(dropTable);
                ps.execute();
                ps.close();
            } else {
                ps.close();
            }

            ps = conn.prepareStatement(createTableSql);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    public TestPage getTestPage(String pageId) {

        String sql = "SELECT * FROM " + TEST_PAGES_TABLE_NAME + " WHERE " + PAGE_ID_DB_COL + " = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pageId);
            TestPage testArea = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                testArea = new TestPage(
                        rs.getString(PAGE_ID_DB_COL),
                        rs.getString(PAGE_NAME_DB_COL),
                        rs.getString(PAGE_FILE_NAME_DB_COL),
                        rs.getString(BROWSER_NAME_DB_COL),
                        rs.getString(BROWSER_VERSION_DB_COL),
                        rs.getInt(BROWSER_WIDTH_DB_COL),
                        rs.getInt(BROWSER_HEIGHT_DB_COL),
                        rs.getInt(IMAGE_HEIGHT_DB_COL)
                );
            }
            rs.close();
            ps.close();
            return testArea;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    public void deleteTestPage(String pageId) {
        String sql = "DELETE FROM " + TEST_PAGES_TABLE_NAME + " WHERE " + PAGE_ID_DB_COL + " = ?";

        Connection conn = null;
        PreparedStatement ps;

        try {

            conn = dataSource.getConnection();

            ps = conn.prepareStatement(sql);
            ps.setString(1, pageId);
            ps.execute();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    public List<TestPage> getTestPages() {

        String sql = "SELECT * FROM " + TEST_PAGES_TABLE_NAME;

        Connection conn = null;
        List<TestPage> pagesList = new LinkedList<TestPage>();

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestPage testPage = new TestPage(
                        rs.getString(PAGE_ID_DB_COL),
                        rs.getString(PAGE_NAME_DB_COL),
                        rs.getString(PAGE_FILE_NAME_DB_COL),
                        rs.getString(BROWSER_NAME_DB_COL),
                        rs.getString(BROWSER_VERSION_DB_COL),
                        rs.getInt(BROWSER_WIDTH_DB_COL),
                        rs.getInt(BROWSER_HEIGHT_DB_COL),
                        rs.getInt(IMAGE_HEIGHT_DB_COL)
                );
                pagesList.add(testPage);
            }
            rs.close();
            ps.close();
            return pagesList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }


    public void addTestPage(TestPage testPage) {
        String sql = "INSERT INTO " + TEST_PAGES_TABLE_NAME + " " +
                "(" + PAGE_ID_DB_COL + ", " + PAGE_NAME_DB_COL + ", " +
                PAGE_FILE_NAME_DB_COL + ", " + BROWSER_WIDTH_DB_COL + ", " +
                BROWSER_HEIGHT_DB_COL + ", " + BROWSER_NAME_DB_COL + ", " +
                BROWSER_VERSION_DB_COL + ", " + IMAGE_HEIGHT_DB_COL +  ") " +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlCheckIfExists = "select count(*) from " + TEST_PAGES_TABLE_NAME + " where " + PAGE_ID_DB_COL + " = ?";
        String sqlDeletePage = "delete from " + TEST_PAGES_TABLE_NAME + " where " + PAGE_ID_DB_COL + " = ?";

        Connection conn = null;
        PreparedStatement ps;

        try {
            conn = dataSource.getConnection();


            ps = conn.prepareStatement(sqlCheckIfExists);
            ps.setString(1, testPage.getPageID());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                //test area with same id exists
                ps.close();
                ps = conn.prepareStatement(sqlDeletePage);
                ps.setString(1, testPage.getPageID());
                ps.execute();
                ps.close();
            } else {
                ps.close();
            }



            ps = conn.prepareStatement(sql);
            ps.setString(1, testPage.getPageID());
            ps.setString(2, testPage.getPageName());
            ps.setString(3, testPage.getFileName());
            ps.setInt(4, testPage.getBrowserWidth());
            ps.setInt(5, testPage.getBrowserHeight());
            ps.setString(6, testPage.getBrowserName());
            ps.setString(7, testPage.getBrowserVersion());
            ps.setInt(8, testPage.getImageHeight());

            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }


    public Map<String, List<TestPage>> getTestPagesGroupedByPageName () {
        List<TestPage> testPages = getTestPages();
        Map<String, List<TestPage>> testPagesMap= new HashMap<String, List<TestPage>>();

        for(TestPage page: testPages) {
            if(!testPagesMap.containsKey(page.getPageName())) {
                testPagesMap.put(page.getPageName(), new LinkedList<TestPage>());
            }
            testPagesMap.get(page.getPageName()).add(page);
        }

        return testPagesMap;
    }

}
