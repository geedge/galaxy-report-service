package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.mapper.ReportResultMapper;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wk1 on 2020/1/2.
 */
@Service
public class MysqlServiceImpl implements MysqlService {
    Log logger = Log.get();

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
        map.put("issuedtime", current_time);
        map.put("rows", rows);

        return rrm.getJobTask(map);

    }

    @Override
    public int updateProcesses(JobEntity job){

            long currentTime = System.currentTimeMillis()/1000;
            job.setLastUpdateTime(currentTime);
            return rrm.updateProcesses(job);
    }



    @Override
    public Map<String,Long> getJobCount()   {
        LocalDate currentDate = LocalDate.now();
        long current_date = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        HashMap map = new HashMap();
        map.put("lastUpdateTime", current_date);
        return rrm.getJobCount(map);
    }

}