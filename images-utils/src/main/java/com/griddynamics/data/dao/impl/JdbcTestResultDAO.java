package com.griddynamics.data.dao.impl;

import com.griddynamics.data.dao.TestAreaDAO;
import com.griddynamics.data.dao.TestResultDAO;
import com.griddynamics.data.model.TestResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lzakharova
 */
public class JdbcTestResultDAO implements TestResultDAO{

    public static final String TEST_RESULTS_TABLE_NAME = "test_results";

    public static final String TEST_ID_DB_COL = "test_id";
    public static final String AREA_ID_DB_COL = "area_id";
    public static final String TEST_SESSION_TS_DB_COL = "test_session_ts";
    public static final String TEST_RUN_TS_DB_COL = "test_run_ts";
    public static final String TEST_RESULT_DB_COL = "test_result";
    public static final String NUMBER_OF_DIFF_PIXELS_DB_COL = "number_of_diff_pixels";
    public static final String IS_VERIFIED_DB_COL = "is_verified";
    public static final String TEST_FOLDER_NAME_DB_COL = "test_folder_name";

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initTestResultsDB() {
        String ifTableExists = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + TEST_RESULTS_TABLE_NAME + "'";

        String dropTable = "drop table " + TEST_RESULTS_TABLE_NAME;

        String createTableSql = "CREATE TABLE " + TEST_RESULTS_TABLE_NAME + "(" +
                TEST_ID_DB_COL + " INTEGER PRIMARY KEY ASC," +
                TEST_SESSION_TS_DB_COL + " TIMESTAMP DEFAULT 0," +
                TEST_RUN_TS_DB_COL + " TIMESTAMP DEFAULT 0," +
                TEST_RESULT_DB_COL + " TINYINT(1) NOT NULL," +
                AREA_ID_DB_COL + " CHAR(128) NOT NULL," +
                NUMBER_OF_DIFF_PIXELS_DB_COL + " INT NOT NULL," +
                IS_VERIFIED_DB_COL + " TINYINT(1) NOT NULL," +
                TEST_FOLDER_NAME_DB_COL + " CHAR(32) NOT NULL)";

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

    public TestResult getTestResult(int testId){

        String sql = "SELECT * FROM " + TEST_RESULTS_TABLE_NAME + " WHERE " + TEST_ID_DB_COL + " = ?";

        Connection conn = null;

        ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
        TestAreaDAO testAreaDAO = (TestAreaDAO) context.getBean("testAreaDAO");

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, testId);
            TestResult testResult = null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                testResult = new TestResult(
                        rs.getInt(TEST_ID_DB_COL),
                        rs.getString(AREA_ID_DB_COL),
                        rs.getString(TEST_SESSION_TS_DB_COL),
                        rs.getString(TEST_RUN_TS_DB_COL),
                        rs.getBoolean(TEST_RESULT_DB_COL),
                        rs.getInt(NUMBER_OF_DIFF_PIXELS_DB_COL),
                        rs.getBoolean(IS_VERIFIED_DB_COL),
                        rs.getString(TEST_FOLDER_NAME_DB_COL)
                );
                testResult.setTestArea(testAreaDAO.getTestArea(rs.getString(AREA_ID_DB_COL)));
            }

            rs.close();
            ps.close();
            return testResult;
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


    public List<TestResult> getAllTestResults(){

        String sql = "select * from " + TEST_RESULTS_TABLE_NAME;

        Connection conn = null;
        List<TestResult> testResultsBySession = new LinkedList<TestResult>();

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestResult testResult = new TestResult(
                        rs.getInt(TEST_ID_DB_COL),
                        rs.getString(AREA_ID_DB_COL),
                        rs.getString(TEST_SESSION_TS_DB_COL),
                        rs.getString(TEST_RUN_TS_DB_COL),
                        rs.getBoolean(TEST_RESULT_DB_COL),
                        rs.getInt(NUMBER_OF_DIFF_PIXELS_DB_COL),
                        rs.getBoolean(IS_VERIFIED_DB_COL),
                        rs.getString(TEST_FOLDER_NAME_DB_COL)
                );
                testResultsBySession.add(testResult);
            }
            rs.close();
            ps.close();
            return testResultsBySession;
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

    public List<TestResult> getTestResultsOnePerSession(){

        String sql = "select * from " + TEST_RESULTS_TABLE_NAME + " group by " + TEST_SESSION_TS_DB_COL;

        Connection conn = null;
        List<TestResult> testResultsBySession = new LinkedList<TestResult>();

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestResult testResult = new TestResult(
                        rs.getInt(TEST_ID_DB_COL),
                        rs.getString(AREA_ID_DB_COL),
                        rs.getString(TEST_SESSION_TS_DB_COL),
                        rs.getString(TEST_RUN_TS_DB_COL),
                        rs.getBoolean(TEST_RESULT_DB_COL),
                        rs.getInt(NUMBER_OF_DIFF_PIXELS_DB_COL),
                        rs.getBoolean(IS_VERIFIED_DB_COL),
                        rs.getString(TEST_FOLDER_NAME_DB_COL)
                );
                testResultsBySession.add(testResult);
            }
            rs.close();
            ps.close();
            Collections.sort(testResultsBySession, Collections.reverseOrder());
            return testResultsBySession;
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

    public List<TestResult> getSessionResults(String sessionTS) {
        String sql = "select * from " + TEST_RESULTS_TABLE_NAME + " WHERE " + TEST_SESSION_TS_DB_COL + " = ?";

        Connection conn = null;
        List<TestResult> testResultsBySession = new LinkedList<TestResult>();

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sessionTS);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestResult testResult = new TestResult(
                        rs.getInt(TEST_ID_DB_COL),
                        rs.getString(AREA_ID_DB_COL),
                        rs.getString(TEST_SESSION_TS_DB_COL),
                        rs.getString(TEST_RUN_TS_DB_COL),
                        rs.getBoolean(TEST_RESULT_DB_COL),
                        rs.getInt(NUMBER_OF_DIFF_PIXELS_DB_COL),
                        rs.getBoolean(IS_VERIFIED_DB_COL),
                        rs.getString(TEST_FOLDER_NAME_DB_COL)
                );
                testResultsBySession.add(testResult);
            }
            rs.close();
            ps.close();
            return testResultsBySession;
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

    public void setVerified(int testId, boolean isVerified) {
        String sql = "update " + TEST_RESULTS_TABLE_NAME + " set " + IS_VERIFIED_DB_COL + " = ? where " + TEST_ID_DB_COL + " = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, isVerified ? "1" : "0");
            ps.setString(2, "" + testId);
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

    public void setTestResult(int testId, boolean isTestPassed) {
        String sql = "update " + TEST_RESULTS_TABLE_NAME + " set " + TEST_RESULT_DB_COL + " = ? where " + TEST_ID_DB_COL + " = ?";
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, isTestPassed ? "1" : "0");
            ps.setString(2, "" + testId);
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

    public void importTestResult(TestResult testResult) {

        String sql = "INSERT INTO " +  TEST_RESULTS_TABLE_NAME +
                " (" + AREA_ID_DB_COL + ", " + TEST_SESSION_TS_DB_COL + ", " + TEST_RUN_TS_DB_COL + ", " +
                TEST_RESULT_DB_COL + ", " + NUMBER_OF_DIFF_PIXELS_DB_COL + ", " + IS_VERIFIED_DB_COL + ", " +
                TEST_FOLDER_NAME_DB_COL + ") " +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)";

        String sqlCheckIfExists = "select count(*) from " + TEST_RESULTS_TABLE_NAME + " where " + TEST_RUN_TS_DB_COL + " = ?";
        String sqlDeleteResultsForTS = "delete from " + TEST_RESULTS_TABLE_NAME + " where " + TEST_RUN_TS_DB_COL + " = ?";

        Connection conn = null;
        PreparedStatement ps;

        try {
            conn = dataSource.getConnection();

            ps = conn.prepareStatement(sqlCheckIfExists);
            ps.setString(1, testResult.getTestRunTs());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                //test results with same run ts exists
                ps.close();
                ps = conn.prepareStatement(sqlDeleteResultsForTS);
                ps.setString(1, testResult.getTestRunTs());
                ps.execute();
                ps.close();
            } else {
                ps.close();
            }



            ps = conn.prepareStatement(sql);
            ps.setString(1, testResult.getTestAreaId());
            ps.setString(2, testResult.getTestSessionTs());
            ps.setString(3, testResult.getTestRunTs());
            ps.setInt(4, testResult.getTestResult() ? 1 : 0);
            ps.setInt(5, testResult.getNumberOfDiffPixels());
            ps.setInt(6, testResult.getIsVerified() ? 1 : 0);
            ps.setString(7, testResult.getTestFolderName());

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



}
