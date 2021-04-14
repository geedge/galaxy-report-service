package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.mapper.ReportResultMapper;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.util.DateUtil;
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
        map.put("endtime", current_time);
        map.put("rows", rows);

        return rrm.getJobTask(map);

    }

    @Override
    public int updateProcesses(JobEntity job){

            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sDate = formatter.format(currentTime);
            job.setOpTime(sDate);
            return rrm.updateProcesses(job);
    }



    @Override
    public Map<String,Long> getJobCount()   {
        String current_time = DateUtil.getDate();
        String today_time = DateUtil.getCurrentDate();
        HashMap map = new HashMap();
        map.put("endtime", current_time);
        map.put("optime", today_time);
        return rrm.getJobCount(map);
    }

}