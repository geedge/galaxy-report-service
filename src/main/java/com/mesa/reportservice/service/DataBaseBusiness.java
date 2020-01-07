package com.mesa.reportservice.service;


import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.util.DateUtil;
import com.mesa.reportservice.util.Logs;
import com.mesa.reportservice.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataBaseBusiness {

    @Autowired
    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet rset;

    public static void clear(PreparedStatement pstmt, Statement stmt, ResultSet rs) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public List<JobEntity> getJobForExcute() {


        String sql = " select * from report_result where status =1  order by end_time ";
        int resultid = 0;
        List<JobEntity> sqllist = new ArrayList<JobEntity>();
        try {
            //connection = manager.getConnection("idb");
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            rset = pstmt.executeQuery();

            while (rset.next()) {
                JobEntity je = new JobEntity();
            //    resultid = rset.getInt("result_id");
                je.setResult_id(rset.getInt("result_id"));
                je.setEnd_time(rset.getString("end_time").trim().substring(0, 19));
                je.setIssued_time(rset.getString("issued_time").trim().substring(0, 19));
                je.setQuery_sql(rset.getString("query_sql").trim());
                sqllist.add(je);
            }
            Logs.debug("-----------success----------" + sql);

        } catch (Exception e) {
            Logs.error(sql + "获取正在执行任务信息失败!");
           // updateStatue(resultid, 7,"获取正在执行任务信息失败");
            e.printStackTrace();
            throw new RuntimeException("获取正在执行任务信息失败!", e);
        } finally {

            clear(pstmt, null, rset);
            //	manager.freeConnection("idb", connection);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Logs.error(e.toString());
                    e.printStackTrace();
                }

            }
        }
        return sqllist;

    }


    public List<JobEntity> getJobLimitRows(int row) {

        String current_time = DateUtil.getDate();
        int resultid = 0;
        String sql = " select * from report_result where status =0 and end_time<'" + current_time + "' order by end_time limit " + row;

        List<JobEntity> sqllist = new ArrayList<JobEntity>();
        try {
            //connection = manager.getConnection("idb");
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                JobEntity je = new JobEntity();
              //  resultid = rset.getInt("result_id");
                je.setExcute_detail("");
                je.setResult_id(rset.getInt("result_id"));
                je.setEnd_time(rset.getString("end_time").trim().substring(0, 19));
                je.setStart_time(rset.getString("start_time").trim().substring(0, 19));
                je.setIssued_time(rset.getString("issued_time").trim().substring(0, 19));
                je.setQuery_sql(rset.getString("query_sql").trim());
                sqllist.add(je);
            }
            Logs.debug("-----------success----------" + sql);

        } catch (Exception e) {
            Logs.error(sql + "获取待执行任务信息失败!");
      //      updateStatue(resultid, 7,"获取待执行任务信息失败");
            e.printStackTrace();
            throw new RuntimeException("获取待执行任务信息失败!", e);


        } finally {
            clear(pstmt, null, rset);

            //	manager.freeConnection("idb", connection);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Logs.error(e.toString());
                    e.printStackTrace();
                }

            }
        }
        return sqllist;
    }


    public int getJobStatueFromId(int id) {

        int statue = 0;
        String sql = " select status from report_result where result_id=" + id;

        try {
            //connection = manager.getConnection("idb");
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                statue = (rset.getInt("status"));
            }
            Logs.debug("-----------success----------" + sql);

        } catch (Exception e) {
            Logs.error(sql + "获取job状态发生异常!");
            e.printStackTrace();
            throw new RuntimeException("获取job状态发生异常!", e);
        } finally {

            clear(pstmt, null, rset);
            //	manager.freeConnection("idb", connection);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Logs.error(e.toString());
                    e.printStackTrace();
                }

            }
        }
        return statue;
    }

    public int updateProcesses(JobEntity job) {
        int a = 0;
        String current_time = DateUtil.getDate();
        String sql="";
        try {
            if(job.getExcute_detail().length()>500) {
                job.setExcute_detail(StringUtil.bSubstring(job.getExcute_detail(), 500));
            }
            sql = "update report_result set op_time ='" + current_time + "',excute_detail = \"" + job.getExcute_detail() + "\", status = " + job.getStatus() + " ,excute_time =" + job.getExcute_time() + ",excute_row =" + job.getExcute_row() + ",excute_process=" + job.getExcute_process() + "  where status = 1 and result_id = " + job.getResult_id();
            //connection = manager.getConnection("idb");
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            a = pstmt.executeUpdate();

        //    connection.commit();
            Logs.debug("-----------success----------" + sql);

        } catch (Exception e) {
            Logs.error(sql + "更新进度条发生异常!");
            e.printStackTrace();
            throw new RuntimeException("更新进度条发生异常!", e);
        } finally {
            clear(pstmt, null, null);

            //	manager.freeConnection("idb", connection);
            if (connection != null) {
                try {
                    connection.commit();
                    connection.close();
                } catch (SQLException e) {
                    Logs.error(e.toString());
                    e.printStackTrace();
                }

            }
        }

        return a;
    }

    public void updateStatue(int id, int status,String excute_detail) {

        String current_time = DateUtil.getDate();
        String sql="";
        try {
            if(excute_detail.length()>500){
                excute_detail=StringUtil.bSubstring(excute_detail,500);
            }
            sql = "update report_result set excute_detail = '"+excute_detail+"',status =" + status + ",op_time ='" + current_time + "'  where result_id = " + id;

            //connection = manager.getConnection("idb");
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(sql);
            int a = pstmt.executeUpdate();

            Logs.debug("-----------success----------" + sql);

        } catch (Exception e) {
            Logs.error(sql + "更新状态发生异常!");
            e.printStackTrace();
            throw new RuntimeException("更新状态发生异常!", e);
        } finally {

            clear(pstmt, null, null);
            //	manager.freeConnection("idb", connection);
            if (connection != null) {
                try {
                    connection.commit();
                    connection.close();
                } catch (SQLException e) {
                    Logs.error(e.toString());
                    e.printStackTrace();
                }

            }
        }
    }


}
