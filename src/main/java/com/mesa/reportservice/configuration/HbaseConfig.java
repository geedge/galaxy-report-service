package com.mesa.reportservice.configuration;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.util.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by wk1 on 2019/5/27.
 */
@Component
@ConfigurationProperties(prefix = "hbase")
public class HbaseConfig {

    private static String columefamily;
    private static String url;
    private static String table;
    private static String colume_job_id;
    private static String colume_query_id;
    private static String colume_query_duration_ms;
    private static String colume_read_rows;
    private static String colume_memory_usage;
    private static String colume_exception;
    private static String colume_sql;

    public static String getHbaseJson(String hbaseid, String responsebody, JobEntity job) {

        String hbasejson = "";
        if (job.getStatus() == 2) {
            hbasejson = "{\"Row\":[{\"key\":\"" + StringUtil.getBase64(hbaseid) + "\", \"Cell\": [{\"column\":\"" + StringUtil.getBase64(columefamily.trim()) + "\",\"$\":\"" + StringUtil.getBase64(responsebody.trim()) + "\"},{\"column\":\"" + StringUtil.getBase64(colume_job_id.trim()) + "\", \"$\":\"" + StringUtil.getBase64(String.valueOf(job.getResultId())) + "\"},{\"column\":\"" + StringUtil.getBase64(colume_query_id.trim()) + "\", \"$\":\"" + StringUtil.getBase64(job.getQuery_id().trim()) + "\"},{\"column\":\"" + StringUtil.getBase64(colume_query_duration_ms.trim()) + "\", \"$\":\"" + StringUtil.getBase64(job.getQuery_duration_ms().trim()) + "\"},{\"column\":\"" + StringUtil.getBase64(colume_read_rows.trim()) + "\", \"$\":\"" + StringUtil.getBase64(String.valueOf(job.getResultRows())) + "\"},{\"column\":\"" + StringUtil.getBase64(colume_memory_usage.trim()) + "\", \"$\":\"" + StringUtil.getBase64(job.getMemory_usage().trim()) + "\"}]}]}";
        } else {
            hbasejson = "{\"Row\":[{\"key\":\"" + StringUtil.getBase64(hbaseid) + "\", \"Cell\": [{\"column\":\"" + StringUtil.getBase64(columefamily.trim()) + "\",\"$\":\"" + StringUtil.getBase64(responsebody.trim()) + "\"},{\"column\":\"" + StringUtil.getBase64(colume_sql.trim()) + "\", \"$\":\"" + StringUtil.getBase64(String.valueOf(job.getQuerySql())) + "\"},{\"column\":\"" + StringUtil.getBase64(colume_exception.trim()) + "\", \"$\":\"" + StringUtil.getBase64(job.getExcuteDetail().trim()) + "\"}]}]}";

        }
        return hbasejson;
    }

    public static String getHbasePostUrl() {

        String hbaseurl = "http://" + url + "/" + table + "/rssss";

        return hbaseurl;
    }

    public void setColumefamily(String columefamily) {
        HbaseConfig.columefamily = columefamily;
    }

    public void setUrl(String url) {
        HbaseConfig.url = url;
    }

    public void setTable(String table) {
        HbaseConfig.table = table;
    }

    public void setColume_query_id(String colume_query_id) {
        HbaseConfig.colume_query_id = colume_query_id;
    }

    public void setColume_query_duration_ms(String colume_query_duration_ms) {
        HbaseConfig.colume_query_duration_ms = colume_query_duration_ms;
    }

    public void setColume_read_rows(String colume_read_rows) {
        HbaseConfig.colume_read_rows = colume_read_rows;
    }

    public void setColume_memory_usage(String colume_memory_usage) {
        HbaseConfig.colume_memory_usage = colume_memory_usage;
    }

    public void setColume_job_id(String colume_job_id) {
        HbaseConfig.colume_job_id = colume_job_id;
    }


    public void setColume_exception(String colume_exception) {
        HbaseConfig.colume_exception = colume_exception;
    }

    public void setColume_sql(String colume_sql) {
        HbaseConfig.colume_sql = colume_sql;
    }
}
