package com.mesa.reportservice.service.impl;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.mapper.ReportResultMapper;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wk1 on 2020/1/2.
 */
@Service
public class MysqlServiceImpl implements MysqlService {

    @Autowired
    public ReportResultMapper rrm;

    @Override
    public List<JobEntity> getJobForExcute() {

        return rrm.getJobForExcute();

    }

    @Override
    public List<JobEntity> getJobTask(int rows) {


            String current_time = DateUtil.getDate();
            HashMap map = new HashMap();
            map.put("endtime", current_time);
            map.put("rows", rows);

            return rrm.getJobTask(map);

    }

    @Override
    public int updateProcesses(JobEntity job) {

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = formatter.format(currentTime);
        java.sql.Timestamp nowTime = java.sql.Timestamp.valueOf(sDate);
        job.setOpTime(sDate);
        return rrm.updateProcesses(job);
    }

    @Override
    public int updateStatue(JobEntity job) {

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = formatter.format(currentTime);
        java.sql.Timestamp nowTime = java.sql.Timestamp.valueOf(sDate);
        job.setOpTime(sDate);
        return rrm.updateStatue(job);
    }
}
