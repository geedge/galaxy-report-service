package com.mesa.reportservice.service;


import com.mesa.reportservice.bean.JobEntity;

import java.util.List;
import java.util.Map;


public interface MysqlService {


    List<JobEntity> getJobForExcute() ;


    List<JobEntity> getJobTask(int Rows) ;


    int updateProcesses(JobEntity job);


    Map<String,Long> getJobCount() ;

}
