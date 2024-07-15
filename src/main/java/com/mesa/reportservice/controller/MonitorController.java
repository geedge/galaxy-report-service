package com.mesa.reportservice.controller;

import com.mesa.reportservice.service.JobService;
import com.mesa.reportservice.service.MonitorService;
import com.mesa.reportservice.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@RestController
@Component
@RequestMapping("/v1/monitor")
public class MonitorController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public R status() {
        return jobService.getJobStatus();
    }

    @GetMapping(value = "/job/{jobId}")
    public R jobDetail(@PathVariable String jobId) {
        Map<String, Object> getJobDetail = jobService.getJobDetail(jobId);
        return R.ok(getJobDetail);
    }
}
