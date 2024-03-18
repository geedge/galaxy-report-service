package com.mesa.reportservice.controller;

import com.mesa.reportservice.bean.MonitorEntity;
import com.mesa.reportservice.service.JobService;
import com.mesa.reportservice.service.MonitorService;
import com.mesa.reportservice.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@RestController
@Component
public class MonitorController {

    @Autowired
    private MonitorService monitorService;
    @Autowired
    private JobService jobService;

    @GetMapping(value = "/monitor")
    public R getJobCount() {
        MonitorEntity jobCount = monitorService.getJobCount();
        return R.ok(jobCount);
    }

    @GetMapping(value = "/monitor/job/{jobId}")
    public R getJobState(@PathVariable String jobId) {
        Map<String, Object> jobState = jobService.getJobDetail(jobId);
        return R.ok(jobState);
    }
}
