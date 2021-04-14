package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.configuration.ZkProperties;
import com.mesa.reportservice.service.MonitorService;
import com.mesa.reportservice.service.ZkService;
import io.micrometer.core.instrument.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
 * Created by wk1 on 2020/1/6.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    Log logger = Log.get();

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
}
