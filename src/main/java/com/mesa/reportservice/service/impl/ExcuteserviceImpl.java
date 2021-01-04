package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.ClickhouseService;
import com.mesa.reportservice.service.ExcuteService;
import com.mesa.reportservice.service.FormatService;
import com.mesa.reportservice.service.MysqlService;
import io.netty.channel.ConnectTimeoutException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;
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



        logger.info("execute queryid=" + job.getQuery_id() + " sql=" + job.getQuerySql() + "mapresult size=" + GlobelConfig.mapresult.size());
        String joburl = getJobUrl(job.getQuerySql(), job.getResultId());
        String killurl = ClickhouseConfig.getKillUrl(job.getQuery_id());
        HttpResult hr = new HttpResult();
        int k = 3;
        do {
            try {
                hr = cs.sendQueryForGet(joburl);
                logger.info("httpcode" + hr.getCode());

                if (hr != null && hr.getCode() != 200) {

                    k = 0;
                    Map mapresult = JSON.parseObject(hr.getBody());
                    job.setExcute_status(Integer.parseInt(mapresult.get("code").toString()));
                    logger.error("excute sql Error ");

                } else {

                    k = 0;
                    Map mapresult = JSON.parseObject(hr.getBody());

                    Map rows = (Map) mapresult.get("statistics");
                    job.setResultRows(Integer.parseInt(rows.get("result_rows").toString()));
                    job.setExcuteRow(Long.parseLong(rows.get("rows_read").toString()));
                    job.setResult(hr.getBody());
                    job.setExcuteTime((int) Float.parseFloat(rows.get("elapsed").toString()));
                    job.setExcute_status(Integer.parseInt(mapresult.get("code").toString()));
                    logger.info("success resultid = "+job.getResultId()+" queryid=" + job.getQuery_id() + " sql=" + job.getQuerySql());

                }
            } catch (SocketTimeoutException e) {
                k--;
                job.setExcuteDetail(e.toString());
                if (k == 0) {
                    job.setExcute_status(500001);
                    job.setExcuteDetail("SQL Execution Error excute query time out");
                    logger.info("timeout resultid = "+job.getResultId()+" queryid=" + job.getQuery_id() + " sql=" + job.getQuerySql());
                } else {
                    logger.info("Socket warn " + e.toString() + "retry time " + (3 - k));
                }


            } catch (ConnectTimeoutException e) {

                job.setExcute_status(555999);
                job.setExcuteDetail(e.toString());
                logger.error("Unknow Error" + e.toString());
                k = 0;

            } catch (OutOfMemoryError e) {

                job.setExcute_status(555999);
                job.setExcuteDetail("result too large");
                logger.error("outofmemery Error" + e.toString());
                k = 0;
            } catch (Exception e) {
                job.setExcute_status(555999);
                job.setExcuteDetail(e.toString());
                logger.error("Unknow Error" + e.toString());
                k = 0;
            }

            if(hr.getCode() != 200) {
                try {
                    cs.QuerySystemForDelete(killurl);
                } catch (Exception e) {
                    logger.error("Kill Query Error" + e.toString());
                }
            }
        }
        while (k > 0);
        job.setStatus(2);

    }

}
