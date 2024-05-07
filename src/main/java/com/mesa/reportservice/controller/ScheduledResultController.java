package com.mesa.reportservice.controller;

import cn.hutool.log.Log;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.configuration.HttpClientPool;
import com.mesa.reportservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wk1
 * @date 2020/1/8
 */
@Component
@EnableScheduling
public class ScheduledResultController {

    Log logger = Log.get();

    protected static ExecutorService pool = Executors.newFixedThreadPool(30);
    @Autowired
    private MysqlService ms;
    @Autowired
    private ExcuteService es;
    @Autowired
    private ExcuteProcessService eps;
    @Autowired
    private ZkService zs;
    @Autowired
    private ClickhouseService cs;
    @Autowired
    private HttpClientPool hc;
    @Autowired
    private GlobelConfig gc;


    @Scheduled(cron = "${scan.result.scheduled.plan}")
    public void getExcuteResult() {
        try {
            if (zs.isMaster()) {
                logger.info("start viewing results");
                //先查询数据库是否有异常状态任务，killquery
                List<JobEntity> joblist = ms.getJobForExcute();
                for (JobEntity jobEntity : joblist) {
                    String sql = jobEntity.getQuerySql().trim();
              /*      sql = sql.replace("$exe_time", "toDateTime('" + jobEntity.getIssuedTime().trim() + "')");
                    sql = sql.replace("$start_time", "toDateTime('" + jobEntity.getStartTime().trim() + "')");
                    sql = sql.replace("$end_time", "toDateTime('" + jobEntity.getEndTime().trim() + "')");*/

                    String queryid = cs.getQueryId(sql);
                    jobEntity.setQueryId(queryid);

                    if (jobEntity.getIsValid() == 0) {
                        eps.killQuery(jobEntity);
                        gc.getMapresult().get(jobEntity.getQueryId()).setIsValid(0);
                    } else if (!gc.getMapresult().containsKey(jobEntity.getQueryId())) {
                        eps.reSet(jobEntity);
                    }
                    if (gc.getMapresult().containsKey(jobEntity.getQueryId())) {
                        if (jobEntity.getIsValid() == 0) {
                            eps.killQuery(jobEntity);
                            gc.getMapresult().get(jobEntity.getQueryId()).setIsValid(0);
                        }
                    } else {
                        eps.reSet(jobEntity);
                    }

                }
                //遍历内存中的任务对状态RUNNING的更新进度，其他更新数据库的状态
                for (Map.Entry<String, JobEntity> entry : gc.getMapresult().entrySet()) {
                    logger.info("key = " + entry.getKey() + ", value = " + entry.getValue().getState());
                    long currentTime = convertToSeconds(System.currentTimeMillis());
                    long excutetime = currentTime - entry.getValue().getStartTime();
                    logger.info("excute time=" + excutetime + "ttl_time=" + convertToSeconds(hc.getSocketTimeout()));
                    if (("RUNNING").equals(entry.getValue().getState()) && excutetime > convertToSeconds(hc.getSocketTimeout())) {
                        entry.getValue().setState("DONE");
                        entry.getValue().setExcuteStatus(500001);
                        eps.killQuery(entry.getValue());
                        eps.updateResultMessage(entry.getValue());
                    } else {
                        if (("RUNNING").equals(entry.getValue().getState())) {
                            eps.updateProcessMessage(entry.getValue());
                        } else {
                            eps.updateResultMessage(entry.getValue());
                        }
                    }
                }
                int rows = gc.getJob_thread() - gc.getMapresult().size();
                if (rows > 0) {
                    List<JobEntity> jobs = ms.getJobTask(rows);
                    for (JobEntity job : jobs) {
                        logger.info("start executing task");
                        long begintime = convertToSeconds(System.currentTimeMillis());
                        job.setStartTime(begintime);
                        String sql = job.getQuerySql().trim();
                      /*  sql = sql.replace("$exe_time", "toDateTime('" + job.getIssuedTime().trim() + "')");
                        sql = sql.replace("$start_time", "toDateTime('" + job.getStartTime().trim() + "')");
                        sql = sql.replace("$end_time", "toDateTime('" + job.getEndTime().trim() + "')");*/
                        job.setQuerySql(sql);
                        job.setState("RUNNING");
                        job.setExcuteStatus(1);
                        job.setResultMessage("EXECUTING");
                        job.setRowsRead(0L);
                        job.setElapsed(0);
                        job.setDoneProgress(0);
                        job.setResultRows(0L);
                        String queryid = "";
                        queryid = cs.getQueryId(sql);
                        job.setQueryId(queryid);
                        if (queryid.equals("") ) {
                            job.setExcuteStatus(0);
                            job.setState("DONE");
                            job.setEndTime(convertToSeconds(System.currentTimeMillis()));
                            //status = 7
                            job.setDoneProgress(1.00f);
                            job.setIsFailed(1);
                            job.setResultMessage("Unknow Error");
                        }
                        if (("RUNNING").equals(job.getState())) {
                            if (ms.updateProcesses(job) != 0) {
                                gc.getMapresult().put(queryid, job);
                                pool.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        es.excuteCkTask(job);
                                    }
                                });
                            } else {
                                logger.error("failed to update database status");

                            }
                        } else {
                            if (ms.updateProcesses(job) != 0) {
                                logger.error("task cannot be executed");
                            } else {
                                logger.error("failed to update database status");

                            }
                        }
                    }
                } else {
                    logger.info("no pending tasks");
                }
            } else {
                if (gc.getMapresult().size() > 0) {
                    for (Map.Entry<String, JobEntity> entry : gc.getMapresult().entrySet()) {
                        logger.info("key = " + entry.getKey() + ", value = " + entry.getValue().getState());
                        eps.killQuery(entry.getValue());
                    }
                    gc.getMapresult().clear();
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private long convertToSeconds(long millis) {
        return millis/1000;
    }
}
