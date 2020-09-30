package com.mesa.reportservice.mapper;

import com.mesa.reportservice.bean.JobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportResultMapper {


    List<JobEntity> getJobForExcute();

    List<JobEntity> getJobTask(Map<String, Object> map);

    int updateProcesses(JobEntity job);

    int updateStatue(JobEntity job);

    Map<String,Long>  getJobCount(Map<String, Object> map);

}