package com.griddynamics.data.dao.impl;

import com.griddynamics.data.dao.TestAreaDAO;
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
public class JdbcTestAreaDAO implements TestAreaDAO {

    public static final String TEST_OBJECTS_TABLE_NAME = "test_objects";

    public static final String AREA_ID_DB_COL = "area_id";
    public static final String AREA_NAME_DB_COL = "area_name";
    public static final String FILE_NAME_DB_COL = "file_name";
    public static final String PAGE_ID_DB_COL = "page_id";
    public static final String WIDTH_DB_COL = "width";
    public static final String HEIGHT_DB_COL = "height";
    public static final String POS_X_DB_COL = "pos_x";
    public static final String POS_Y_DB_COL = "pos_y";
    public static final String COUNT_FROM_TOP_COL = "count_from_top";


    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void initTestAreaDB() {
        String ifTableExists = "SELECT count(*) FROM sqlite_master " +
                "WHERE type='table' AND name='" + TEST_OBJECTS_TABLE_NAME + "'";

        String dropTable = "drop table " + TEST_OBJECTS_TABLE_NAME;


        String createTableSql = "CREATE TABLE " + TEST_OBJECTS_TABLE_NAME + "(" +
                AREA_ID_DB_COL + " CHAR(128) NOT NULL, " +
                AREA_NAME_DB_COL + " CHAR(128) NOT NULL, " +
                FILE_NAME_DB_COL + " CHAR(128) NOT NULL, " +
                PAGE_ID_DB_COL + " CHAR(128) NOT NULL, " +
                WIDTH_DB_COL + " INT NOT NULL, " +
                HEIGHT_DB_COL + " INT NOT NULL, " +
                POS_X_DB_COL + " INT, " +
                POS_Y_DB_COL + " INT, " +
                COUNT_FROM_TOP_COL + " TINYINT(1) NOT NULL)";

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
                } catch (SQLException e) {
                }
            }
        }
    }

    public TestArea getTestArea(String areaId) {

        String sql = "SELECT * FROM " + TEST_OBJECTS_TABLE_NAME + " WHERE " + AREA_ID_DB_COL + " = ?";

        Connection conn = null;

        JdbcTestPageDAO testPageDAO = new JdbcTestPageDAO();
        testPageDAO.setDataSource(dataSource);

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, areaId);
            TestArea testArea = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                testArea = new TestArea(
                        rs.getString(AREA_ID_DB_COL),
                        rs.getString(AREA_NAME_DB_COL),
                        rs.getString(FILE_NAME_DB_COL),
                        testPageDAO.getTestPage(rs.getString(PAGE_ID_DB_COL)),
                        rs.getInt(WIDTH_DB_COL),
                        rs.getInt(HEIGHT_DB_COL),
                        rs.getInt(POS_X_DB_COL),
                        rs.getInt(POS_Y_DB_COL),
                        rs.getBoolean(COUNT_FROM_TOP_COL)
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
                } catch (SQLException e) {
                }
            }
        }
    }

    public void updateTestArea(TestArea testArea) {

        String sql = "update " + TEST_OBJECTS_TABLE_NAME + " set " + AREA_NAME_DB_COL + " = ? , " +
                PAGE_ID_DB_COL + " = ? , " + WIDTH_DB_COL + " = ? , " + HEIGHT_DB_COL + " = ? , " +
                POS_X_DB_COL + " = ? , " + POS_Y_DB_COL + " = ? , " + COUNT_FROM_TOP_COL + " = ? " +
                "where " + AREA_ID_DB_COL + " = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, testArea.getAreaName());
            ps.setString(2, testArea.getPage().getPageID());
            ps.setString(3, "" + testArea.getWidth());
            ps.setString(4, "" + testArea.getHeight());
            ps.setString(5, "" + testArea.getPosX());
            ps.setString(6, "" + testArea.getPosY());
            ps.setString(7, testArea.getCountFromTop() ? "1" : "0");
            ps.setString(8, "" + testArea.getAreaId());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public void deleteTestArea(String areaId) {
        String sql = "DELETE FROM " + TEST_OBJECTS_TABLE_NAME + " WHERE " + AREA_ID_DB_COL + " = ?";

        Connection conn = null;
        PreparedStatement ps;

        try {

            conn = dataSource.getConnection();

            ps = conn.prepareStatement(sql);
            ps.setString(1, areaId);
            ps.execute();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public List<TestArea> getTestAreas() {

        String sql = "SELECT * FROM " + TEST_OBJECTS_TABLE_NAME;

        Connection conn = null;
        List<TestArea> areasList = new LinkedList<TestArea>();

        JdbcTestPageDAO testPageDAO = new JdbcTestPageDAO();
        testPageDAO.setDataSource(dataSource);

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestArea testArea = new TestArea(
                        rs.getString(AREA_ID_DB_COL),
                        rs.getString(AREA_NAME_DB_COL),
                        rs.getString(FILE_NAME_DB_COL),
                        testPageDAO.getTestPage(rs.getString(PAGE_ID_DB_COL)),
                        rs.getInt(WIDTH_DB_COL),
                        rs.getInt(HEIGHT_DB_COL),
                        rs.getInt(POS_X_DB_COL),
                        rs.getInt(POS_Y_DB_COL),
                        rs.getBoolean(COUNT_FROM_TOP_COL)
                );
                areasList.add(testArea);
            }
            rs.close();
            ps.close();
            return areasList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }


    public Map<String, List<TestArea>> getTestAreasGroupedByPage() {
        List<TestArea> testAreas = getTestAreas();
        Map<String, List<TestArea>> testAreasMap = new HashMap<String, List<TestArea>>();

        for (TestArea testArea : testAreas) {
            if (!testAreasMap.containsKey(testArea.getPage().getPageID())) {
                testAreasMap.put(testArea.getPage().getPageID(), new LinkedList<TestArea>());
            }
            testAreasMap.get(testArea.getPage().getPageID()).add(testArea);
        }

        return testAreasMap;
    }


    public List<TestArea> getTestAreasForPage(String pageId) {
        String sql = "SELECT * FROM " + TEST_OBJECTS_TABLE_NAME + " WHERE " + PAGE_ID_DB_COL + " = ?";

        Connection conn = null;
        List<TestArea> areasList = new LinkedList<TestArea>();

        JdbcTestPageDAO testPageDAO = new JdbcTestPageDAO();
        testPageDAO.setDataSource(dataSource);

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pageId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestArea testArea = new TestArea(
                        rs.getString(AREA_ID_DB_COL),
                        rs.getString(AREA_NAME_DB_COL),
                        rs.getString(FILE_NAME_DB_COL),
                        testPageDAO.getTestPage(rs.getString(PAGE_ID_DB_COL)),
                        rs.getInt(WIDTH_DB_COL),
                        rs.getInt(HEIGHT_DB_COL),
                        rs.getInt(POS_X_DB_COL),
                        rs.getInt(POS_Y_DB_COL),
                        rs.getBoolean(COUNT_FROM_TOP_COL)
                );
                areasList.add(testArea);
            }
            rs.close();
            ps.close();
            return areasList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }


    public void addTestArea(TestArea testArea) {
        String sql = "INSERT INTO " + TEST_OBJECTS_TABLE_NAME + " " +
                "(" + AREA_ID_DB_COL + ", " + AREA_NAME_DB_COL + ", " + FILE_NAME_DB_COL + ", " +
                PAGE_ID_DB_COL + ", " + WIDTH_DB_COL + ", " +
                HEIGHT_DB_COL + ", " + POS_X_DB_COL + ", " + POS_Y_DB_COL + ", " + COUNT_FROM_TOP_COL + ") " +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlCheckIfExists = "select count(*) from " + TEST_OBJECTS_TABLE_NAME + " where " + AREA_ID_DB_COL + " = ?";
        String sqlDeleteArea = "delete from " + TEST_OBJECTS_TABLE_NAME + " where " + AREA_ID_DB_COL + " = ?";

        Connection conn = null;
        PreparedStatement ps;

        try {
            conn = dataSource.getConnection();


            ps = conn.prepareStatement(sqlCheckIfExists);
            ps.setString(1, testArea.getAreaId());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                //test area with same id exists
                ps.close();
                ps = conn.prepareStatement(sqlDeleteArea);
                ps.setString(1, testArea.getAreaId());
                ps.execute();
                ps.close();
            } else {
                ps.close();
            }


            ps = conn.prepareStatement(sql);
            ps.setString(1, testArea.getAreaId());
            ps.setString(2, testArea.getAreaName());
            ps.setString(3, testArea.getFileName());
            ps.setString(4, testArea.getPage().getPageID());
            ps.setInt(5, testArea.getWidth());
            ps.setInt(6, testArea.getHeight());
            ps.setInt(7, testArea.getPosX());
            ps.setInt(8, testArea.getPosY());
            ps.setInt(9, testArea.getCountFromTop() ? 1 : 0);

            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

}
