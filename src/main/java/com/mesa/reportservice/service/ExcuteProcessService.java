package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.JobEntity;

import java.io.IOException;

/**
 * Created by wk1 on 2020/1/8.
 */
public interface ExcuteProcessService {

    void updateResultMessage(JobEntity job);
    void reSet(JobEntity jobEntity);
    void updateProcessMessage(JobEntity job) throws IOException;
    void killQuery(JobEntity jobEntity) throws IOException;

}
