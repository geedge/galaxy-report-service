package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by wk1 on 2020/1/8.
 */
@Service

public class ExcuteProcessServiceImpl implements ExcuteProcessService {
    Log logger = Log.get();


    @Autowired
    private ClickhouseService cs;
    @Autowired
    private MysqlService ms;
    @Autowired
    private HbaseService hs;
    @Autowired
    private MonitorService mons;
    @Autowired
    private GlobelConfig gc;
    @Override
    public void updateResultMessage(JobEntity je) {


        try {
            if (je.getIsValid() == 0) {
                je.setStatus(9);
                je.setExcuteDetail("CANCEL");
            } else {


                if (je.getExcute_status() >= 20000000 && je.getExcute_status() < 30000000) {

                    Boolean isok = saveToHbase(je);
                    if (isok) {
                        je.setExcuteDetail("SUCCESS");
                        je.setExcuteProcess(100);
                        je.setStatus(2);
                        logger.info("success save to hbase query_id=" + je.getQuery_id() + " resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                    } else {
                        je.setStatus(5);
                        je.setExcuteDetail("Write Data Error");
                        mons.addFail();
                        logger.error("save hbase error "+je.getExcute_status()+" query_id=" + je.getQuery_id() + " resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                    }
                } else if (je.getExcute_status() >= 40000000 && je.getExcute_status() < 50000000) {

                    je.setStatus(3);
                    je.setExcuteDetail("Param Syntax Error");
                    logger.error("Param Syntax Error "+je.getExcute_status()+" query_id=" + je.getQuery_id() + " resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());

                } else if (je.getExcute_status() == 50001300) {

                    je.setStatus(4);
                    je.setExcuteDetail("SQL Execution Error");
                    logger.error("SQL Execution Error ! ErrorCode  "+je.getExcute_status()+" query_id=" + je.getQuery_id() + "  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());

                } else {

                    je.setStatus(7);
                    je.setExcuteDetail("Unknow Error");
                    logger.error("Unknow Error ! ErrorCode  "+je.getExcute_status()+" query_id=" + je.getQuery_id() + " resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                }

            }

            int number = 0;
            int z = 3;
            do {
                number = ms.updateProcesses(je);
                z--;
            }
            while (number != 1 && z >= 0);
        } catch (Exception e) {
            je.setStatus(10);
            je.setExcuteDetail("Database Error");
            ms.updateProcesses(je);
            logger.error("save database error resultid =" + je.getResultId()+" queryid=" + je.getResultId() + e.toString());

        } finally {
            saveToMonitor(je);
            gc.getMapresult().remove(je.getQuery_id());
        }


    }

    @Override
    public void reSet(JobEntity jobEntity) {

            jobEntity.setStatus(0);
            jobEntity.setExcuteDetail("Re Execution");
            ms.updateProcesses(jobEntity);

    }

    @Override
    public void killQuery(JobEntity jobEntity) throws IOException {

            cs.queryForCancel(jobEntity.getQuery_id());

    }

    @Override
    public void updateProcessMessage(JobEntity job) throws IOException {

            HttpResult hr = cs.queryForProcess(job.getQuery_id());
            if (hr!=null) {
                String rs = hr.getBody().trim();
                Map data = JSON.parseObject(rs);

                if (!rs.isEmpty() && !rs.equals("")) {
                    List listdata = (List) data.get("data");
                    if (null != listdata && listdata.size() > 0) {
                        Map map = JSON.parseObject(JSON.toJSONString(listdata.get(0)));
                        long read_rows = Long.parseLong(map.get("rows_read").toString());
                        float elapsed = Float.parseFloat(map.get("elapsed").toString());
                        double persent = Double.parseDouble(map.get("percent").toString());
                        int process = (int) (persent * 100);
                        job.setExcuteTime((int) elapsed);
                        job.setExcuteRow(read_rows);
                        if(job.getExcuteProcess()<process){
                            job.setExcuteProcess(process);
                        }
                        if (job.getExcuteRow() != 0 || job.getExcuteTime() != 0) {
                            ms.updateProcesses(job);
                        }
                    } else {
                        logger.info("responsedata is null");
                    }
                } else {
                    logger.error("responsebody is null");
                }
            }
            else{
                logger.error("responsebody is null");
            }

    }
    /**
     * 结果存入hbase
     */
    public Boolean saveToHbase(JobEntity entity) {
        int k = 3;
        Boolean isok = false;
        do {
            try {
                k--;
                isok = hs.put(entity);
                break;
            } catch (Exception e) {
                logger.error("写入hbase报错重试次数" + (3 - k) + e.toString());
                k--;
            }
        }
        while (k >= 0);
        return isok;
    }

    /**
     * promethus记录结果
     */
    public void saveToMonitor(JobEntity entity) {
            if (entity.getStatus() == 2) {

                mons.addSuccess();
            } else {
                mons.addFail();
            }
    }
}
