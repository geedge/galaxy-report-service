package com.mesa.reportservice.controller;

import cn.hutool.log.Log;
import com.alibaba.fastjson.JSONArray;
import com.mesa.reportservice.bean.MonitorEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.service.ZkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mesa.reportservice.configuration.ZkProperties;

import java.util.Map;


/**
 * Created by wk on 2020/4/16.
 */
@RestController
@Component
public class MonitorController {

    @Autowired
    private MysqlService mysqlService;
    @Autowired
    private ZkService zs;
    @Autowired
    private GlobelConfig gc;
    @Autowired
            private ZkProperties zkProperties;

    Log logger = Log.get();


    @GetMapping(value = "/monitor")
    public String getReportStatus() {


        String json="";
        try {
            MonitorEntity me = new MonitorEntity();
            Map<String,Long> numMap = mysqlService.getJobCount();
            me.setQueueJobNum(numMap.get("queueNum"));
            me.setExcuteingJobNum(numMap.get("excuteingNum"));
            me.setTodaySuccessJobNum(numMap.get("todaySuccessNum"));
            me.setTodayErrorJobNum(numMap.get("todayErrorNum"));
            me.setJoblist(gc.getMapresult());
            if(zs.isMaster()){
                me.setStatus("active");
            }
            else{
                me.setStatus("standby");
            }
            gc.getMapresult().size();
            Object obj = JSONArray.toJSON(me);
            json = obj.toString();
        } catch (Exception e) {
            logger.error(e.toString());
            json=e.toString();
        }

            return json;
    }

}
