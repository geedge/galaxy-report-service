package com.mesa.reportservice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.SchedulerProperties;
import com.mesa.reportservice.enums.JobStates;
import com.mesa.reportservice.exception.SQSCode;
import com.mesa.reportservice.exception.SQSException;
import com.mesa.reportservice.service.*;
import com.mesa.reportservice.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class ExecuteProcessServiceImpl implements ExecuteProcessService {

    private static final Log logger = LogFactory.get();

    @Autowired
    private QueryGatewayService queryGatewayService;

    @Autowired
    private JobService jobService;

    @Autowired
    private HBaseService hBaseService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Override
    public void updateResultMessage(JobEntity jobEntity) {
        String msg = "";
        jobEntity.setState(JobStates.DONE.getValue());
        jobEntity.setEndTime(System.currentTimeMillis()/1000);
        jobEntity.setDoneProgress(1.00f);
        jobEntity.setIsFailed(1);
        String resultMessage = jobEntity.getResultMessage();
        try {
            if (jobEntity.getIsValid() == 0) {
                jobEntity.setResultMessage(Constant.CANCEL);
            } else {
                if (jobEntity.getExecuteStatus() == Constant.JOB_SUCCESS) {
                    Boolean isOk = saveToHBase(jobEntity);
                    if (isOk) {
                        jobEntity.setResultMessage(Constant.OK);
                        jobEntity.setIsFailed(0);
                        logger.info(MessageFormat.format(Constant.LOG_SUCCESS_SAVE_TO_HBASE,jobEntity.getQueryId(),jobEntity.getJobId(),jobEntity.getQuerySql()));
                    } else {
                        jobEntity.setResultMessage(StrUtil.concat(false,Constant.WRITE_DATA_ERROR,StrUtil.COLON,resultMessage));
                        monitorService.addFail();
                        msg = MessageFormat.format(SQSCode.TEMPLATE_SAVE_HBASE_ERROR.getMsg(),jobEntity.getExecuteStatus(),jobEntity.getQueryId(),jobEntity.getJobId(),jobEntity.getQuerySql());
                        logger.error(msg);
                    }
                } else if (jobEntity.getExecuteStatus() >= Constant.JOB_BAD_REQUEST_THRESHOLD && jobEntity.getExecuteStatus() < Constant.JOB_INTERNAL_SERVER_ERROR_THRESHOLD) {
                    jobEntity.setResultMessage(StrUtil.concat(false,Constant.PARAM_SYNTAX_ERROR,StrUtil.COLON,resultMessage));
                    msg = MessageFormat.format(SQSCode.PARAM_SYNTAX_ERROR.getMsg(),jobEntity.getExecuteStatus(),jobEntity.getQueryId(),jobEntity.getJobId(),jobEntity.getQuerySql());
                    logger.error(msg);
                } else if (jobEntity.getExecuteStatus() == Constant.JOB_SQL_EXECUTION_ERROR_CODE) {
                    jobEntity.setResultMessage(StrUtil.concat(false,Constant.SQL_EXECUTION_ERROR,StrUtil.COLON,resultMessage));
                    msg = MessageFormat.format(SQSCode.TEMPLATE_SQL_EXECUTION_ERROR.getMsg(),jobEntity.getExecuteStatus(),jobEntity.getQueryId(),jobEntity.getJobId(),jobEntity.getQuerySql());
                    logger.error(msg);
                } else {
                    jobEntity.setResultMessage(StrUtil.concat(false,Constant.UNKNOWN_ERROR,StrUtil.COLON,resultMessage));
                    msg = MessageFormat.format(SQSCode.UNKNOWN_ERROR.getMsg(),jobEntity.getQueryId(),jobEntity.getJobId(),jobEntity.getQuerySql());
                    logger.error(msg);
                }
            }
            int number = 0;
            int z = 3;
            do {
                number = jobService.updateProcesses(jobEntity);
                z--;
            }
            while (number != 1 && z >= 0);
        } catch (Exception e) {
            jobEntity.setResultMessage(StrUtil.concat(false,Constant.DATABASE_ERROR,StrUtil.COLON,e.getMessage()));
            jobService.updateProcesses(jobEntity);
            msg = MessageFormat.format(SQSCode.TEMPLATE_SAVE_DATABASE_ERROR.getMsg(),jobEntity.getQueryId(),jobEntity.getJobId(),e.getMessage());
            logger.error(msg);
        } finally {
            saveToMonitor(jobEntity);
            schedulerProperties.getMapResult().remove(jobEntity.getQueryId());
        }
    }

    @Override
    public void reSet(JobEntity jobEntity){
        jobEntity.setState(JobStates.PENDING.getValue());
        jobEntity.setResultMessage(Constant.RE_EXECUTION);
        jobService.updateProcesses(jobEntity);
    }

    @Override
    public void killQuery(JobEntity jobEntity){
        queryGatewayService.deleteJob(jobEntity.getQueryId());
    }

    @Override
    public void updateProcessMessage(JobEntity jobEntity){
        HttpResult httpResult = queryGatewayService.queryJobProcess(jobEntity.getQueryId());
        if (httpResult!=null) {
            String result = httpResult.getBody().trim();
            Map data = JSON.parseObject(result);
            if (!result.isEmpty() && !result.equals("")) {
                List listData = (List) data.get("data");
                if (null != listData && listData.size() > 0) {
                    Map map = JSON.parseObject(JSON.toJSONString(listData.get(0)));
                    long readRows = Long.parseLong(map.get("rows_read").toString());
                    float elapsed = Float.parseFloat(map.get("elapsed").toString());
                    float percent = Float.parseFloat(map.get("percent").toString());
                    jobEntity.setElapsed((int) elapsed);
                    jobEntity.setRowsRead(readRows);
                    if(jobEntity.getDoneProgress()<percent){
                        jobEntity.setDoneProgress(percent);
                    }
                    if (jobEntity.getRowsRead() != 0 || jobEntity.getElapsed() != 0) {
                        jobService.updateProcesses(jobEntity);
                    }
                } else {
                    logger.error(SQSCode.RESPONSE_DATA_ISNULL.getMsg());
                    throw new SQSException(SQSCode.RESPONSE_DATA_ISNULL);
                }
            } else {
                logger.error(SQSCode.RESPONSE_BODY_ISNULL.getMsg());
                throw new SQSException(SQSCode.RESPONSE_BODY_ISNULL);
            }
        } else {
            logger.error(SQSCode.RESPONSE_BODY_ISNULL.getMsg());
            throw new SQSException(SQSCode.RESPONSE_BODY_ISNULL);
        }
    }
    /**
     * 结果存入hbase
     */
    public Boolean saveToHBase(JobEntity jobEntity) {
        int k = 3;
        Boolean isok = false;
        do {
            try {
                k--;
                isok = hBaseService.put(jobEntity);
                break;
            } catch (Exception e) {
                logger.error(MessageFormat.format(SQSCode.WRITE_HBASE_ERROR.getMsg(),(3 - k),e.toString()));
                k--;
            }
        }
        while (k >= 0);
        return isok;
    }


    public void saveToMonitor(JobEntity jobEntity) {
        if ((JobStates.DONE.getValue()).equals(jobEntity.getState()) && jobEntity.getIsFailed() == 0 && jobEntity.getIsValid() == 1) {
            monitorService.addSuccess();
        } else {
            monitorService.addFail();
        }
    }
}
