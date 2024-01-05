package com.mesa.reportservice.service;


import org.springframework.stereotype.Service;

/**
 * Created by wk1 on 2020/1/6.
 */
public interface MonitorService {


     void  addSuccess();

     void  addFail();

     String getReportStatus();
}