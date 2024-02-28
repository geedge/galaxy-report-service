package com.mesa.reportservice.controller;

import com.mesa.reportservice.service.ScheduledResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Component
@EnableScheduling
public class ScheduledResultController {

    @Autowired
    private ScheduledResultService scheduledResultService;

    @Scheduled(cron = "${scan.result.scheduled.plan}")
    public void execute() {
        scheduledResultService.execute();
    }
}
