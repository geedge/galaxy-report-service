package com.mesa.reportservice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.HttpClientPool;
import com.mesa.reportservice.configuration.SchedulerProperties;
import com.mesa.reportservice.configuration.ThreadPoolConfig;
import com.mesa.reportservice.enums.JobStates;
import com.mesa.reportservice.exception.SQSCode;
import com.mesa.reportservice.exception.SQSException;
import com.mesa.reportservice.service.*;
import com.mesa.reportservice.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class ScheduledResultServiceImpl implements ScheduledResultService {

    private static final Log logger = LogFactory.get();

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    @Autowired
    private JobService jobService;

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private ExecuteProcessService executeProcessService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private QueryGatewayService queryGatewayService;

    @Autowired
    private HttpClientPool httpClientPool;

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Override
    public void execute() {
        try {
            if (zookeeperService.isMaster()) {
                logger.info(Constant.LOG_START_VIEWING_RESULTS);
                updateAbnormalJob();

                updateRunningJob();

                updatePendingJob();
            } else {
                if (schedulerProperties.getMapResult().size() > 0) {
                    for (Map.Entry<String, JobEntity> entry : schedulerProperties.getMapResult().entrySet()) {
                        logger.info("key = " + entry.getKey() + ", value = " + entry.getValue().getState());
                        executeProcessService.killQuery(entry.getValue());
                    }
                    schedulerProperties.getMapResult().clear();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SQSException(e.getMessage());
        }
    }

    private void updateAbnormalJob(){
        //先查询数据库是否有异常状态任务，killquery
        List<JobEntity> jobList = jobService.getRunningJob();
        for (JobEntity jobEntity : jobList) {
            String sql = jobEntity.getQuerySql().trim();
            String queryId = queryGatewayService.getQueryId(sql);
            jobEntity.setQueryId(queryId);
            if (schedulerProperties.getMapResult().containsKey(jobEntity.getQueryId()) && jobEntity.getIsValid() == 0) {
                executeProcessService.killQuery(jobEntity);
                schedulerProperties.getMapResult().get(jobEntity.getQueryId()).setIsValid(0);
            }
            if(!schedulerProperties.getMapResult().containsKey(jobEntity.getQueryId())) {
                executeProcessService.reSet(jobEntity);
            }
        }
    }

    private void updateRunningJob(){
        //遍历内存中的任务对状态RUNNING的更新进度，其他更新数据库的状态
        for (Map.Entry<String, JobEntity> entry : schedulerProperties.getMapResult().entrySet()) {
            logger.info("key = " + entry.getKey() + ", value = " + entry.getValue().getState());
            long currentTime = convertToSeconds(System.currentTimeMillis());
            long executeTime = currentTime - entry.getValue().getStartTime();
            logger.info("execute time=" + executeTime + "ttlTime=" + convertToSeconds(httpClientPool.getSocketTimeout()));
            if (JobStates.RUNNING.getValue().equals(entry.getValue().getState()) && executeTime > convertToSeconds(httpClientPool.getSocketTimeout())) {
                entry.getValue().setState(JobStates.DONE.getValue());
                entry.getValue().setExecuteStatus(Constant.JOB_EXECUTION_TIMEOUT);
                executeProcessService.killQuery(entry.getValue());
                executeProcessService.updateResultMessage(entry.getValue());
            } else {
                if (JobStates.RUNNING.getValue().equals(entry.getValue().getState())) {
                    executeProcessService.updateProcessMessage(entry.getValue());
                } else {
                    executeProcessService.updateResultMessage(entry.getValue());
                }
            }
        }
    }

    private void updatePendingJob() {
        int rows = schedulerProperties.getJobThread() - schedulerProperties.getMapResult().size();
        if (rows > 0) {
            List<JobEntity> jobs = jobService.getJobTask(rows);
            for (JobEntity job : jobs) {
                logger.info(Constant.LOG_START_EXECUTING_TASKS);
                long startTime = convertToSeconds(System.currentTimeMillis());
                job.setStartTime(startTime);
                String sql = job.getQuerySql().trim();
                job.setQuerySql(sql);
                job.setState(JobStates.RUNNING.getValue());
                job.setExecuteStatus(1);
                job.setResultMessage(Constant.EXECUTING);
                job.setRowsRead(0L);
                job.setElapsed(0);
                job.setDoneProgress(0);
                job.setResultRows(0L);
                String queryId = "";
                queryId = queryGatewayService.getQueryId(sql);
                job.setQueryId(queryId);
                if (queryId.equals("") ) {
                    job.setExecuteStatus(0);
                    job.setState(JobStates.DONE.getValue());
                    job.setEndTime(convertToSeconds(System.currentTimeMillis()));
                    job.setDoneProgress(1.00f);
                    job.setIsFailed(1);
                    job.setResultMessage(StrUtil.concat(false,Constant.UNKNOWN_ERROR,": queryId is null"));
                }
                if ((JobStates.RUNNING.getValue()).equals(job.getState())) {
                    if (jobService.updateProcesses(job) != 0) {
                        schedulerProperties.getMapResult().put(queryId, job);
                        threadPoolConfig.getThreadPoolTaskExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                executeService.queryQgw(job);
                            }
                        });
                    } else {
                        logger.error(SQSCode.UPDATE_STATUS_ERROR.getMsg());
                    }
                } else {
                    if (jobService.updateProcesses(job) != 0) {
                        logger.error(SQSCode.TASK_CANNOT_EXECUTED.getMsg());
                    } else {
                        logger.error(SQSCode.UPDATE_STATUS_ERROR.getMsg());
                    }
                }
            }
        } else {
            logger.info(Constant.LOG_NO_PENDING_TASKS);
        }
    }

    private long convertToSeconds(long millis) {
        return millis/1000;
    }

}
