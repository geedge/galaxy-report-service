package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.SchedulerProperties;
import com.mesa.reportservice.enums.JobStates;
import com.mesa.reportservice.exception.SQSCode;
import com.mesa.reportservice.exception.SQSException;
import com.mesa.reportservice.service.QueryGatewayService;
import com.mesa.reportservice.service.ExecuteService;
import com.mesa.reportservice.util.Constant;
import io.netty.channel.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;
import java.util.Map;


/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class ExecuteServiceImpl implements ExecuteService {

    private static final Log logger = LogFactory.get();

    @Autowired
    private QueryGatewayService queryGatewayService;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Override
    public void queryQgw(JobEntity job) {
        logger.info("execute queryId=" + job.getQueryId() + " sql=" + job.getQuerySql() + "mapResult size=" + schedulerProperties.getMapResult().size());
        HttpResult httpResult = null;
        int k = 3;
        do {
            try {
                httpResult = queryGatewayService.queryJob(job.getQuerySql());
                if (httpResult != null) {
                    Map mapresult = JSON.parseObject(httpResult.getBody());
                    int queryStatus = Integer.parseInt(mapresult.get("status").toString());
                    job.setExecuteStatus(Integer.parseInt(mapresult.get("code").toString()));
                    job.setResultMessage(mapresult.get("message").toString());
                    logger.info("httpCode=" + httpResult.getCode() +" status="+queryStatus);
                    if (httpResult.getCode() == HttpStatus.OK.value() && queryStatus == HttpStatus.OK.value()) {
                        k = 0;
                        mapresult = JSON.parseObject(httpResult.getBody());
                        Map rows = (Map) mapresult.get("statistics");
                        job.setResultRows(Long.parseLong(rows.get("result_rows").toString()));
                        job.setRowsRead(Long.parseLong(rows.get("rows_read").toString()));
                        job.setBytesRead(Long.parseLong(rows.get("bytes_read").toString()));
                        job.setResultBytes(Long.parseLong(rows.get("result_bytes").toString()));
                        job.setResult(httpResult.getBody());
                        job.setElapsed((int) Float.parseFloat(rows.get("elapsed").toString()));
                        logger.info("success resultId = " + job.getJobId() + " queryId=" + job.getQueryId() + " sql=" + job.getQuerySql());
                    } else {
                        k = 0;
                        logger.error("SQL Execution Error ");
                    }
                } else {
                    throw new SocketTimeoutException();
                }
            } catch (SocketTimeoutException e) {
                k--;
                job.setResultMessage(e.getMessage());
                if (k == 0) {
                    job.setExecuteStatus(Constant.JOB_EXECUTION_TIMEOUT);
                    job.setResultMessage(Constant.QUERY_TIME_OUT);
                    logger.info("timeout resultId = " + job.getJobId() + " queryId=" + job.getQueryId() + " sql=" + job.getQuerySql());
                } else {
                    logger.info("Socket warn " + e.getMessage() + "retry time " + (3 - k));
                }
            } catch (ConnectTimeoutException e) {
                job.setExecuteStatus(Constant.JOB_ERROR);
                job.setResultMessage(e.getMessage());
                logger.error(SQSCode.UNKNOWN_ERROR.getMsg() + e.getMessage());
                k = 0;
            } catch (OutOfMemoryError e) {
                job.setExecuteStatus(Constant.JOB_ERROR);
                job.setResultMessage(e.getMessage());
                logger.error(SQSCode.OUT_OF_MEMORY_ERROR.getMsg() + e.getMessage());
                k = 0;
            } catch (Exception e) {
                job.setExecuteStatus(Constant.JOB_ERROR);
                job.setResultMessage(e.getMessage());
                logger.error(SQSCode.UNKNOWN_ERROR.getMsg() + e.getMessage());
                k = 0;
            }
            try {
                queryGatewayService.deleteJob(job.getQueryId());
            } catch (Exception e) {
                logger.error(SQSCode.KILL_QUERY_ERROR.getMsg() + e.getMessage());
                throw new SQSException(SQSCode.KILL_QUERY_ERROR);
            }
        }
        while (k > 0);
        job.setState(JobStates.DONE.getValue());
        job.setEndTime(System.currentTimeMillis()/1000);
    }
}
