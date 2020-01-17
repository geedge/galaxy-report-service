package com.mesa.reportservice.controller;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.service.ExcuteService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.service.ZkService;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
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

    protected static ExecutorService pool = Executors.newFixedThreadPool(20);
    @Autowired
    private MysqlService ms;
    @Autowired
    private ExcuteService es;
    @Autowired
    private ZkService zs;

    @Scheduled(cron = "${scan.task.scheduled.plan}")
    public void excuteSqlToCk() {

        if (zs.isMaster()) {

            int rows = GlobelConfig.job_thread- GlobelConfig.mapresult.size();
            if (rows>0) {

                List<JobEntity> jobs = ms.getJobTask(rows);


                for(JobEntity job :jobs) {

                    Logs.debug("开始执行任务");

                    pool.execute(new Runnable() {
                        @Override
                        public void run() {
                            es.excuteCkTask(job);
                        }
                    });
                }

                } else {
                    Logs.debug("无待执行任务");
                }
            }
        }


}
