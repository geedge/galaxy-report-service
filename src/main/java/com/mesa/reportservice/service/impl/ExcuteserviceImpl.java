package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.ClickhouseService;
import com.mesa.reportservice.service.ExcuteService;
import com.mesa.reportservice.service.MysqlService;
import io.netty.channel.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;
import java.util.Map;


/**
 * Created by wk1 on 2020/1/8.
 */
@Service
public class ExcuteserviceImpl implements ExcuteService {
    Log logger = Log.get();


    @Autowired
    private ClickhouseService cs;
    @Autowired
    private MysqlService ms;
    @Autowired
    private GlobelConfig gc;

    @Override
    public void excuteCkTask(JobEntity job) {


        logger.info("execute queryid=" + job.getQueryId() + " sql=" + job.getQuerySql() + "mapresult size=" + gc.getMapresult().size());
        HttpResult hr = new HttpResult();
        int k = 3;
        do {
            try {
                hr = cs.queryForExcute(job.getQuerySql());
                if (hr != null) {
                    Map mapresult = JSON.parseObject(hr.getBody());
                    int query_status = Integer.parseInt(mapresult.get("status").toString());
                    logger.info("httpcode=" + hr.getCode() +" status="+query_status);
                    if (hr.getCode() == 200 && query_status == 200) {

                        k = 0;
                        mapresult = JSON.parseObject(hr.getBody());

                        Map rows = (Map) mapresult.get("statistics");
                        job.setResultRows(Long.parseLong(rows.get("result_rows").toString()));
                        job.setRowsRead(Long.parseLong(rows.get("rows_read").toString()));
                        job.setBytesRead(Long.parseLong(rows.get("bytes_read").toString()));
                        job.setResultBytes(Long.parseLong(rows.get("result_bytes").toString()));
                        job.setResult(hr.getBody());
                        job.setElapsed((int) Float.parseFloat(rows.get("elapsed").toString()));
                        job.setExcuteStatus(Integer.parseInt(mapresult.get("code").toString()));
                        logger.info("success resultid = " + job.getJobId() + " queryid=" + job.getQueryId() + " sql=" + job.getQuerySql());

                    } else {
                        k = 0;
                        job.setExcuteStatus(Integer.parseInt(mapresult.get("code").toString()));
                        logger.error("excute sql Error ");
                    }
                } else {
                    throw new SocketTimeoutException();
                }
            } catch (SocketTimeoutException e) {
                k--;
                job.setResultMessage(e.toString());
                if (k == 0) {
                    job.setExcuteStatus(500001);
                    job.setResultMessage("SQL Execution Error excute query time out");
                    logger.info("timeout resultid = " + job.getJobId() + " queryid=" + job.getQueryId() + " sql=" + job.getQuerySql());
                } else {
                    logger.info("Socket warn " + e.toString() + "retry time " + (3 - k));
                }


            } catch (ConnectTimeoutException e) {

                job.setExcuteStatus(555999);
                job.setResultMessage(e.toString());
                logger.error("Unknow Error" + e.toString());
                k = 0;

            } catch (OutOfMemoryError e) {

                job.setExcuteStatus(555999);
                job.setResultMessage("result too large");
                logger.error("outofmemery Error" + e.toString());
                k = 0;
            } catch (Exception e) {
                job.setExcuteStatus(555999);
                job.setResultMessage(e.toString());
                logger.error("Unknow Error" + e.toString());
                k = 0;
            }
            try {
                cs.queryForCancel(job.getQueryId());
            } catch (Exception e) {
                logger.error("Kill Query Error" + e.toString());
            }

        }
        while (k > 0);
        job.setState("DONE");
        job.setEndTime(System.currentTimeMillis()/1000);
    }

}
