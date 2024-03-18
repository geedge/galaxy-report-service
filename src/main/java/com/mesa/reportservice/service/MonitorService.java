package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.MonitorEntity;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
public interface MonitorService {

     void  addSuccess();

     void  addFail();

     MonitorEntity getJobCount();
}