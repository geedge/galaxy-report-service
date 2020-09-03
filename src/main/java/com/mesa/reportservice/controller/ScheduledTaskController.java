package com.mesa.reportservice.controller;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.service.ExcuteService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.service.ZkService;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wk1 on 2020/1/8.
 */
@Component
@EnableScheduling
public class ScheduledTaskController {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @Autowired
    private MysqlService ms;
    @Autowired
    private ExcuteService es;
    @Autowired
    private ZkService zs;

    @Async("taskExecutor")
    @Scheduled(cron = "${scan.task.scheduled.plan}")
    public void excuteSqlToCk()  {

        try {
            if (zs.isMaster()) {

                int rows = GlobelConfig.job_thread - GlobelConfig.mapresult.size();
                logger.info("row=" + rows);
                if (rows > 0) {
                    List<JobEntity> jobs = ms.getJobTask(1);
                    for (JobEntity job : jobs) {
                        logger.info("开始执行任务");
                        es.excuteCkTask(job);

                    }
                } else {
                    logger.debug("无待执行任务");
                }
            } else {
                logger.debug("zk 不是主节点");

            }
        }
        catch (Exception e){
            logger.error(e.toString());
        }
        }


}
