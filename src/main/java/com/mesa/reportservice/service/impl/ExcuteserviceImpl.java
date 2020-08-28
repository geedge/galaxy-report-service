package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.service.ClickhouseService;
import com.mesa.reportservice.service.ExcuteService;
import com.mesa.reportservice.service.FormatService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.configuration.GlobelConfig;
import io.netty.channel.ConnectTimeoutException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import static com.mesa.reportservice.configuration.ClickhouseConfig.getJobUrl;

/**
 * Created by wk1 on 2020/1/8.
 */
@Service
public class ExcuteserviceImpl implements ExcuteService {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());


    @Autowired
    private ClickhouseService cs;
    @Autowired
    private MysqlService ms;

    @Autowired
    private FormatService fs;

    @Override
    public void excuteCkTask(JobEntity job) {


        String sql = job.getQuerySql().trim();
        sql = sql.replace("$exe_time", "toDateTime('" + job.getIssuedTime().trim() + "')");
       // sql = sql.replace("$start_time", "toDateTime('" + job.getStartTime().trim() + "')");
      //  sql = sql.replace("$end_time", "toDateTime('" + job.getEndTime().trim() + "')");
        //job.setFormatSql(sql);
        job.setQuerySql(sql);
        String queryid = DigestUtils.md5Hex(job.getResultId() + sql);

     /*   job.setFormatSql(sql);
        try {
            String formateUrl = getFormatUrl(sql);
            sql = fs.doFormat(formateUrl);
        } catch (Exception e) {
            sql="";
            logger.error(e.toString());
        }

        if (sql.equals("ok") ||  sql.equals("")) {
            job.setStatus(3);
            job.setExcuteTime(0);
            job.setExcuteDetail("SQL Syntax Error parsing SQL Error");
            ms.updateStatue(job);


        } else {*/
            job.setQuery_id(queryid);
            job.setQuerySql(sql);
            job.setStatus(1);
            job.setExcute_status(1);
            job.setExcuteDetail("EXECUTING");
            //JobEntity jt = (JobEntity) job.clone();
            GlobelConfig.mapresult.put(queryid, job);
            ms.updateStatue(job);
            logger.info("execute queryid=" + queryid + " sql=" + sql + "mapresult size=" + GlobelConfig.mapresult.size());
            String joburl = getJobUrl(job.getQuerySql(), job.getResultId());
            String killurl = ClickhouseConfig.getKillUrl(job.getQuery_id());
            HttpResult hr = new HttpResult();
            int k = 3;
            do {
                try {
                    hr = cs.sendQueryForGet(joburl);

                    logger.info("httpcode" + hr.getCode());

                    if (hr.getCode() != 200 ) {

                        k=0;
                        Map mapresult = JSON.parseObject(hr.getBody());
                        job.setExcute_status(Integer.parseInt(mapresult.get("code").toString()));
                        logger.error("excute sql Error ");

                    } else {

                        k=0;
                        Map mapresult = JSON.parseObject(hr.getBody());

                        Map rows = (Map) mapresult.get("statistics");
                       // Integer resultrows = (Integer) rows.get("result_rows");
                        job.setResultRows(Integer.parseInt(rows.get("result_rows").toString()));
                        job.setExcuteRow(Long.parseLong(rows.get("rows_read").toString()));
                        job.setResult(hr.getBody());
                        job.setExcuteTime(Math.round( Float.parseFloat(rows.get("elapsed").toString())));

                        job.setExcute_status(Integer.parseInt(mapresult.get("code").toString()));
                        logger.info("success queryid=" + queryid + " sql=" + sql);

                    }
                } catch (SocketTimeoutException e) {
                    k--;
                    job.setExcuteDetail(e.toString());
                    if(k==0) {
                        job.setExcute_status(500001);
                        job.setExcuteDetail("SQL Execution Error excute query time out");
                        logger.info("timeout queryid=" + queryid + " sql=" + sql);
                    }
                    else{
                        logger.info("Socket warn " + e.toString() + "retry time " + (3 - k));
                    }


                } catch (ConnectTimeoutException e){

                    job.setExcute_status(555999);
                    job.setExcuteDetail(e.toString());
                    logger.error("Unknow Error" + e.toString());
                    k=0;

                }
                catch (OutOfMemoryError e){

                    job.setExcute_status(555999);
                    job.setExcuteDetail("result too large");
                    logger.error("outofmemery Error" + e.toString());
                    k=0;
                }
                catch (Exception e) {
                    job.setExcute_status(555999);
                    job.setExcuteDetail(e.toString());
                    logger.error("Unknow Error" + e.toString());
                    k=0;
                }

                try {
                    cs.QuerySystemForDelete(killurl);
                } catch (Exception e) {
                    logger.error("Kill Query Error" + e.toString());
                }
            }
            while (k > 0);
            job.setStatus(2);
          //  GlobelConfig.mapresult.put(job.getQuery_id(), job);



    }

}
