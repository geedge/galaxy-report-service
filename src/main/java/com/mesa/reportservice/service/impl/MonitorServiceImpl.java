package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.mesa.reportservice.bean.MonitorEntity;
import com.mesa.reportservice.configuration.SchedulerProperties;
import com.mesa.reportservice.service.JobService;
import com.mesa.reportservice.service.MonitorService;
import com.mesa.reportservice.service.ZookeeperService;
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
    private JobService mysqlService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private SchedulerProperties schedulerProperties;

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

    @Override
    public String getJobCount() {
        String json="";
        MonitorEntity monitorEntity = new MonitorEntity();
        Map<String,Long> numMap = mysqlService.getJobCount();
        monitorEntity.setQueueJobNum(numMap.get("queueNum"));
        monitorEntity.setExecutingJobNum(numMap.get("executingNum"));
        monitorEntity.setTodaySuccessJobNum(numMap.get("todaySuccessNum"));
        monitorEntity.setTodayErrorJobNum(numMap.get("todayErrorNum"));
        monitorEntity.setJobList(schedulerProperties.getMapResult());
        if(zookeeperService.isMaster()){
            monitorEntity.setStatus("active");
        }else{
            monitorEntity.setStatus("standby");
        }
        Object obj = JSONArray.toJSON(monitorEntity);
        json = obj.toString();
        return json;
    }
}
