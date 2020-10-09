package com.mesa.reportservice.controller;

import com.alibaba.fastjson.JSONArray;
import com.mesa.reportservice.bean.MonitorEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.MysqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * Created by wk on 2020/4/16.
 */
@RestController
@Component
public class MonitorController {

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    @Autowired
    private MysqlService mysqlService;


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
            me.setJoblist(GlobelConfig.mapresult);
            GlobelConfig.mapresult.size();
            Object obj = JSONArray.toJSON(me);
            json = obj.toString();
        } catch (Exception e) {
            logger.error(e.toString());
            json=e.toString();
        }

            return json;
    }


}
