package com.mesa.reportservice.service;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
public interface MonitorService {

     void  addSuccess();

     void  addFail();

     String getJobCount();
}