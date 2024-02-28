package com.mesa.reportservice.controller;

import com.mesa.reportservice.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


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

    @GetMapping(value = "/monitor")
    public String getJobCount() {
        return monitorService.getJobCount();
    }
}
