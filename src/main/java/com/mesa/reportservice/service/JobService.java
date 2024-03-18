package com.mesa.reportservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.mesa.reportservice.bean.JobEntity;

import java.util.List;
import java.util.Map;


/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
public interface JobService  extends IService<JobEntity> {

    List<JobEntity> getRunningJob() ;

    List<JobEntity> getJobTask(int Rows) ;

    int updateProcesses(JobEntity job);

    Map<String,Long> getJobCount();

    Map<String,Object> getJobDetail(String jobId);
}
