package com.mesa.reportservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mesa.reportservice.bean.JobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Mapper
public interface JobMapper extends BaseMapper<JobEntity> {


    List<JobEntity> getRunningJob();

    List<JobEntity> getJobTask(Map<String, Object> map);

    int updateProcesses(JobEntity job);

    int updateStatue(JobEntity job);

    Map<String,Object> getJobCount(long lastUpdateTime);

    Map<String,Object> getJobDetail(String jobId);
}