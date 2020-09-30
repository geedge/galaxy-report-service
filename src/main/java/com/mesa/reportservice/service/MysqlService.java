package com.mesa.reportservice.service;


import com.mesa.reportservice.bean.JobEntity;

import java.util.List;
import java.util.Map;


public interface MysqlService {


    List<JobEntity> getJobForExcute() throws Exception;


    List<JobEntity> getJobTask(int Rows) throws Exception;


    int updateProcesses(JobEntity job);

    int updateStatue(JobEntity job);

    Map<String,Long> getJobCount()  throws Exception;

    }
