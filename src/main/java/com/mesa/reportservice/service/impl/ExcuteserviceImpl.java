package com.mesa.reportservice.service.impl;

import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.service.ClickhouseService;
import com.mesa.reportservice.service.ExcuteService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.util.FormatSqlUtil;
import com.mesa.reportservice.util.Logs;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketException;

import static com.mesa.reportservice.configuration.ClickhouseConfig.getJobUrl;

/**
 * Created by wk1 on 2020/1/8.
 */
@Service
public class ExcuteserviceImpl implements ExcuteService {


    @Autowired
    private ClickhouseService cs;
    @Autowired
    private MysqlService ms;

    @Override
    public void excuteCkTask(JobEntity job) {


        String sql = job.getQuerySql().trim();
        String queryid = DigestUtils.md5Hex(job.getResultId() + sql);
        sql = sql.replace("$exe_time", "toDateTime('" + job.getIssuedTime().toString().trim() + "')");
      //  sql = sql.replace("$start_time", "toDateTime('" + job.getStartTime().toString().trim() + "')");
      //  sql = sql.replace("$end_time", "toDateTime('" + job.getEndTime().toString().trim() + "')");


        sql = FormatSqlUtil.formatSql(sql);
        if (sql.equals("") || sql == null) {
            job.setStatus(3);
            job.setExcuteDetail("SQL Syntax Error parsing SQL Error");
            ms.updateStatue(job);
        } else {
            job.setQuery_id(queryid);
            job.setQuerySql(sql);
            job.setQuery_id(queryid);
            job.setQuerySql(sql);
            job.setStatus(1);
            job.setResult("");
            job.setExcuteDetail("EXECUTING");
            JobEntity jt = (JobEntity) job.clone();
            GlobelConfig.mapresult.put(queryid, jt);
            ms.updateStatue(job);
            Logs.info("execute queryid=" + queryid + " sql=" + sql + "mapresult size=" + GlobelConfig.mapresult.size());
            String joburl = getJobUrl(job.getQuerySql(), job.getQuery_id());
            HttpResult hr = new HttpResult();
            int k = 3;
            do {
                try {
                    hr = cs.sendQueryForPost(joburl);
                    String killurl = ClickhouseConfig.getKillUrl(job.getQuery_id());
                    cs.sendQueryForPost(killurl);
                    if (hr.getCode() != 200 && hr.getBody().toLowerCase().contains("timeout")) {
                        k--;
                    } else {
                        break;
                    }
                } catch (SocketException e) {
                    k--;
                    job.setExcuteDetail(e.toString());
                    job.setStatus(6);
                    Logs.error("Unknow Error" + e.toString());
                } catch (Exception e) {
                    job.setStatus(6);
                    job.setExcuteDetail(e.toString());
                    Logs.error("Unknow Error" + e.toString());
                    break;
                }
            }
            while (k > 0);

            job.setResult(hr.getBody());
            if (hr.getCode() == 200 && !hr.getBody().equals("") && !hr.getBody().isEmpty()) {
                job.setResult(hr.getBody());
                job.setExcuteDetail("Query SUCCESS");
                job.setExcuteTime(0);
                job.setExcuteProcess(100);
                job.setStatus(2);
                Logs.info("success queryid=" + queryid + " sql=" + sql);
            } else {
                job.setResult("");
                job.setStatus(6);
                job.setExcuteDetail(hr.getBody());
                Logs.error("Unknow Error Query " + hr.getBody());
                Logs.info("error queryid=" + queryid + " sql=" + sql);

            }
            GlobelConfig.mapresult.put(job.getQuery_id(), job);

        }

    }

}
