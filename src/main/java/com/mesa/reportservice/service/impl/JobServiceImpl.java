package com.mesa.reportservice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.SchedulerProperties;
import com.mesa.reportservice.exception.SQSCode;
import com.mesa.reportservice.exception.SQSException;
import com.mesa.reportservice.mapper.JobMapper;
import com.mesa.reportservice.service.JobService;
import com.mesa.reportservice.service.ZookeeperService;
import com.mesa.reportservice.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, JobEntity> implements JobService {

    @Autowired
    private SchedulerProperties schedulerProperties;

    @Autowired
    private ZookeeperService zookeeperService;

    @Override
    public List<JobEntity> getRunningJob() {
        return this.baseMapper.getRunningJob();
    }

    @Override
    public List<JobEntity> getJobTask(int rows) {
        Map map = new HashMap<String,Object>();
        map.put("rows", rows);
        return this.baseMapper.getJobTask(map);
    }

    @Override
    public int updateProcesses(JobEntity job){
        long currentTime = System.currentTimeMillis()/1000;
        job.setLastUpdateTime(currentTime);
        return this.baseMapper.updateProcesses(job);
    }

    @Override
    public R getJobStatus() {
        Map<String,Object> numMap = getJobCount();
        Map<String, Object> status = null;
        try {
            status = zookeeperService.status();
        } catch (Exception e) {
            e.printStackTrace();
        }
        R r = new R();
        r.put("data",numMap);
        r.putAll(status);
        return r;
    }

    private Map<String,Object> getJobCount(){
        long lastUpdateTime = System.currentTimeMillis()/1000 - convertDaysToSeconds(1);
        Map<String, Object> jobCount = this.baseMapper.getJobCount(lastUpdateTime);
        LambdaQueryWrapper<JobEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(JobEntity::getJobId).eq(JobEntity::getIsFailed,1).ge(JobEntity::getLastUpdateTime,lastUpdateTime);
        List<String> failedJobIdList = this.baseMapper.selectObjs(lambdaQueryWrapper).stream().map(o -> (String) o).collect(Collectors.toList());
        Map<String, Object> jobs = new HashMap<>();
        jobs.put("running",schedulerProperties.getMapResult());
        jobs.put("failed",failedJobIdList);
        jobCount.put("jobs",jobs);
        return jobCount;
    }

    @Override
    public Map<String,Object> getJobDetail(String jobId){
        Map<String,Object> jobState = this.baseMapper.getJobDetail(jobId);
        if (ObjectUtil.isEmpty(jobState)) {
            throw new SQSException(SQSCode.JOB_IS_NOT_EXIST);
        }
        return jobState;
    }

    private long convertDaysToSeconds(Integer days) {
        return 3600L*24*days;
    }
}