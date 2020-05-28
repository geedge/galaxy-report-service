package com.mesa.reportservice.controller;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.ExcuteProcessService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.service.ZkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.mesa.reportservice.configuration.GlobelConfig.*;

/**
 * @author wk1
 * @date 2020/1/8
 */
@Component
@EnableScheduling
public class ScheduledResultController {

    @Autowired
    private MysqlService ms;

    @Autowired
    private ExcuteProcessService eps;
    @Autowired
    private ZkService zs;

    @Scheduled(cron = "${scan.result.scheduled.plan}")
    public void getExcuteResult() {
        if (zs.isMaster()) {

            List<JobEntity> joblist = ms.getJobForExcute();
            for(JobEntity jobEntity:joblist) {
                eps.getTaskProcess(jobEntity);
            }
        }
    }

}
