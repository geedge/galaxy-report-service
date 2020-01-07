package com.mesa.reportservice.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by wk1 on 2019/5/17.
 */
@Component
@ConfigurationProperties(prefix = "ck")
public class ClickhouseConfig {

    private static String task_ip;
    private static String task_database;
    private static String task_user;
    private static String task_user_password;
    private static String log_ip;
    // private static String log_database;
    private static String log_user;
    private static String log_user_password;

    public static String getJobUrl(String sql, String query_id) {

        String url = "http://" + task_ip.trim() + "/?user=" + task_user.trim() + "&password=" + task_user_password.trim()
                + "&database=" + task_database.trim() + "&query_id=" + query_id + "&query=";
        String jobsql = "";

        try {
            jobsql = url + URLEncoder.encode(sql + " FORMAT JSON", "utf8");


        } catch (UnsupportedEncodingException e) {
            Logs.error(e.toString());
        }
        return jobsql;
    }

    public static String getProcessUrl(String query_id) {

        String url = "http://" + log_ip.trim() + "/?user=" + log_user.trim() + "&password=" + log_user_password.trim()
                + "&database=system" + "&query=";
        String processsql = "";
        // String logsql = "select elapsed,total_rows_approx,read_rows from `system`.processes where query_id='" + query_id + "' FORMAT JSONEachRow";
        try {

            String sql = URLEncoder.encode("select elapsed,total_rows_approx,read_rows from `system`.processes where query_id='" + query_id + "' FORMAT JSONEachRow", "utf8");
            processsql = url + sql;
        } catch (UnsupportedEncodingException e) {
            Logs.error(e.toString());

        }
        return processsql;
    }

    public static String getFinishUrl(String query_id) {

        String url = "http://" + log_ip.trim() + "/?user=" + log_user.trim() + "&password=" + log_user_password.trim()
                + "&database=system" + "&query=";
        String finishsql = "";
        // String logsql = "select elapsed,total_rows_approx,read_rows from `system`.processes where query_id='" + query_id + "' FORMAT JSONEachRow";
        try {

            String sql = URLEncoder.encode("select CAST(type, 'Int8') as type,read_rows,query_duration_ms,query,exception,memory_usage,event_time from `system`.query_log where type>1 and query_id='" + query_id + "' order by event_time desc limit 1 FORMAT JSONEachRow", "utf8");
            finishsql = url + sql;
        } catch (UnsupportedEncodingException e) {
            Logs.error(e.toString());

        }
        return finishsql;
    }

    public static String getKillUrl(String query_id) {

        String url = "http://" + task_ip.trim() + "/?user=" + task_user.trim() + "&password=" + task_user_password.trim()
                + "&query=";

        String killsql = "";

        try {

            String sql = URLEncoder.encode("KILL QUERY WHERE query_id='" + query_id + "'", "utf8");
            killsql = url + sql;
        } catch (UnsupportedEncodingException e) {
            Logs.error(e.toString());

        }

        return killsql;
    }

    public void setTask_ip(String task_ip) {
        ClickhouseConfig.task_ip = task_ip;
    }

    public void setLog_ip(String log_ip) {
        ClickhouseConfig.log_ip = log_ip;
    }

    public void setTask_database(String task_database) {
        ClickhouseConfig.task_database = task_database;
    }

    public void setTask_user(String task_user) {
        ClickhouseConfig.task_user = task_user;
    }

    public void setTask_user_password(String task_user_password) {
        ClickhouseConfig.task_user_password = task_user_password;
    }

    public void setLog_user(String log_user) {
        ClickhouseConfig.log_user = log_user;
    }

    public void setLog_user_password(String log_user_password) {
        ClickhouseConfig.log_user_password = log_user_password;
    }
}
