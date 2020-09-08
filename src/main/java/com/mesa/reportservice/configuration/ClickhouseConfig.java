package com.mesa.reportservice.configuration;

import com.mesa.reportservice.util.Logs;
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

    private static String gateway_ip;
    public static String getJobUrl(String sql, Integer reportId) {
        String url = "http://" + gateway_ip.trim() + "/?option=long-term&reportId=" + reportId + "&query=";
        String jobsql = "";
        try {

            sql = URLEncoder.encode(sql , "utf8").replaceAll("\\+", "%20");
            jobsql = url + sql;

        } catch (UnsupportedEncodingException e) {
            Logs.error(e.toString());
        }
        return jobsql;
    }

    public static String getProcessUrl(String query_id) {

        String url = "http://" + gateway_ip.trim() + "/?query=";
        String processsql = "";
        try {
            String sql = URLEncoder.encode("select elapsed,total_rows_approx,read_rows from `system`.processes where query_id='" + query_id + "'", "utf8").replaceAll("\\+", "%20");
            processsql = url + sql;
        } catch (UnsupportedEncodingException e) {
            Logs.error(e.toString());
        }
        return processsql;
    }

    public static String getFinishUrl(String query_id) {

        String url = "http://" + gateway_ip.trim() + "/?query=";
        String finishsql = "";
        try {
            String sql = URLEncoder.encode("select CAST(type, 'Int8') as type,read_rows,query_duration_ms,query,exception,memory_usage,event_time,result_rows,result_bytes from `system`.query_log where type>1 and query_id='" + query_id + "' order by event_time desc limit 1", "utf8").replaceAll("\\+", "%20");
            finishsql = url + sql;
        } catch (UnsupportedEncodingException e) {
            Logs.error(e.toString());
        }
        return finishsql;
    }

    public static String getKillUrl(String query_id) {

        String url = "http://" + gateway_ip.trim() + "/sys/engine/tasks/"+query_id;
        return url;
    }
    public  void setGateway_ip(String gateway_ip) {
        ClickhouseConfig.gateway_ip = gateway_ip;
    }
}
