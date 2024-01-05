package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONArray;
import com.mesa.reportservice.bean.MonitorEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.MonitorService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.service.ZkService;
import io.micrometer.core.instrument.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by wk1 on 2020/1/6.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    public static final Log logger = LogFactory.get();

    @Autowired
    private MysqlService mysqlService;

    @Autowired
    private ZkService zkService;

    @Autowired
    private GlobelConfig globelConfig;

    @Autowired
    MeterRegistry registry;

    private Counter counter_report_success;
    private Counter counter_report_fail;

    @PostConstruct
    public void init(){

        counter_report_success = registry.counter("report_success_count", "reportJob", "report_success");

        counter_report_fail = registry.counter("report_fail_count", "reportJob", "report_error");
    }
    @Override
    public void addSuccess() {
        counter_report_success.increment();
    }
    @Override
    public void addFail() {
        counter_report_fail.increment();
    }


    public MeterRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getReportStatus(){
        String json="";
        MonitorEntity me = new MonitorEntity();
        Map<String,Long> numMap = mysqlService.getJobCount();
        me.setQueueJobNum(numMap.get("queueNum"));
        me.setExcuteingJobNum(numMap.get("excuteingNum"));
        me.setTodaySuccessJobNum(numMap.get("todaySuccessNum"));
        me.setTodayErrorJobNum(numMap.get("todayErrorNum"));
        me.setJoblist(globelConfig.getMapresult());
        if(zkService.isMaster()){
            me.setStatus("active");
        }else{
            me.setStatus("standby");
        }
        globelConfig.getMapresult().size();
        Object obj = JSONArray.toJSON(me);
        json = obj.toString();
        return json;
    }
}
