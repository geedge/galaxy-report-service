package com.mesa.reportservice.controller;

import com.mesa.reportservice.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by wk on 2020/4/16.
 */
@RestController
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping(value = "/monitor")
    public String getReportStatus() {
        return monitorService.getReportStatus();
    }

}
