package com.mesa.reportservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.mapper.JobMapper;
import com.mesa.reportservice.service.JobService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, JobEntity> implements JobService {

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
    public Map<String,Long> getJobCount(){
        LocalDate currentDate = LocalDate.now();
        long lastUpdateTime = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        Map map = new HashMap<String,Object>();
        map.put("lastUpdateTime", lastUpdateTime);
        return this.baseMapper.getJobCount(map);
    }

}