package com.mesa.reportservice.service.impl;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.mapper.ReportResultMapper;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.util.DateUtil;
import com.mesa.reportservice.util.Logs;
import com.mesa.reportservice.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wk1 on 2020/1/2.
 */
@Service
public class MysqlServiceImpl implements MysqlService {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @Autowired
    public ReportResultMapper rrm;

    @Override
    public List<JobEntity> getJobForExcute() throws Exception{

        return rrm.getJobForExcute();

    }

    @Override
    public List<JobEntity> getJobTask(int rows) throws Exception{


        String current_time = DateUtil.getDate();
        HashMap map = new HashMap();
        map.put("endtime", current_time);
        map.put("rows", rows);

        return rrm.getJobTask(map);

    }

    @Override
    public int updateProcesses(JobEntity job){

        try {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sDate = formatter.format(currentTime);
            java.sql.Timestamp nowTime = java.sql.Timestamp.valueOf(sDate);
            job.setOpTime(sDate);
            return rrm.updateProcesses(job);
        }
        catch(Exception e){
            logger.error(e.toString());
            return 0;
        }
    }

    @Override
    public int updateStatue(JobEntity job) {
        try {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sDate = formatter.format(currentTime);
            java.sql.Timestamp nowTime = java.sql.Timestamp.valueOf(sDate);
            job.setOpTime(sDate);
            return rrm.updateStatue(job);
        }
        catch(Exception e){
            logger.error(e.toString());
            return 0;
        }
    }

    @Override
    public Map<String,Long> getJobCount() throws Exception {
        String current_time = DateUtil.getDate();
        String today_time = DateUtil.getCurrentDate();
        HashMap map = new HashMap();
        map.put("endtime", current_time);
        map.put("optime", today_time);
        return rrm.getJobCount(map);
    }

}