package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.JobEntity;

import java.io.IOException;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
public interface ExecuteProcessService {

    void updateResultMessage(JobEntity job);

    void reSet(JobEntity jobEntity);

    void updateProcessMessage(JobEntity job);

    void killQuery(JobEntity jobEntity);

}
