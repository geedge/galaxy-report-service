package com.mesa.reportservice.service.impl;

import com.mesa.reportservice.configuration.SchedulerProperties;
import com.mesa.reportservice.service.JobService;
import com.mesa.reportservice.service.MonitorService;
import com.mesa.reportservice.service.ZookeeperService;
import com.mesa.reportservice.util.R;
import io.micrometer.core.instrument.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter counterReportSuccess;

    private Counter counterReportFail;

    @PostConstruct
    public void init(){
        counterReportSuccess = meterRegistry.counter("report_success_count", "reportJob", "report_success");
        counterReportFail = meterRegistry.counter("report_fail_count", "reportJob", "report_error");
    }

    @Override
    public void addSuccess() {
        counterReportSuccess.increment();
    }

    @Override
    public void addFail() {
        counterReportFail.increment();
    }
}
