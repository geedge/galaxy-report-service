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
        je.setState("DONE");
        je.setEndTime(System.currentTimeMillis()/1000);
        je.setDoneProgress(1.00f);
        je.setIsFailed(1);
        try {
            if (je.getIsValid() == 0) {
                //status = 9
                je.setResultMessage("CANCEL");
            } else {
                if (je.getExcuteStatus() >= 20000000 && je.getExcuteStatus() < 30000000) {

                    Boolean isok = saveToHbase(je);
                    if (isok) {
                        //status = 2
                        je.setResultMessage("OK");
                        je.setIsFailed(0);
                        logger.info("success save to hbase query_id=" + je.getQueryId() + " job_id =" + je.getJobId() + " excute_sql=" + je.getQuerySql());
                    } else {
                        //status = 5
                        je.setResultMessage("Write Data Error");
                        mons.addFail();
                        logger.error("save hbase error "+je.getExcuteStatus()+" query_id=" + je.getQueryId() + " job_id =" + je.getJobId() + " excute_sql=" + je.getQuerySql());
                    }
                } else if (je.getExcuteStatus() >= 40000000 && je.getExcuteStatus() < 50000000) {
                    //status = 3
                    je.setResultMessage("Param Syntax Error");
                    logger.error("Param Syntax Error "+je.getExcuteStatus()+" query_id=" + je.getQueryId() + " job_id =" + je.getJobId() + " excute_sql=" + je.getQuerySql());

                } else if (je.getExcuteStatus() == 50001300) {
                    //status = 4
                    je.setResultMessage("SQL Execution Error");
                    logger.error("SQL Execution Error ! ErrorCode  "+je.getExcuteStatus()+" query_id=" + je.getQueryId() + "  job_id =" + je.getJobId() + " excute_sql=" + je.getQuerySql());

                } else {
                    //status = 7
                    je.setResultMessage("Unknow Error");
                    logger.error("Unknow Error ! ErrorCode  "+je.getExcuteStatus()+" query_id=" + je.getQueryId() + " job_id =" + je.getJobId() + " excute_sql=" + je.getQuerySql());
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
            //status = 10
            je.setResultMessage("Database Error");
            ms.updateProcesses(je);
            logger.error("save database error job_id =" + je.getJobId()+" query_id=" + je.getQueryId() + e.toString());

        } finally {
            saveToMonitor(je);
            gc.getMapresult().remove(je.getQueryId());
        }


    }

    @Override
    public void reSet(JobEntity jobEntity) {
            //status = 0
            jobEntity.setState("PENDING");
            jobEntity.setResultMessage("Re Execution");
            ms.updateProcesses(jobEntity);

    }

    @Override
    public void killQuery(JobEntity jobEntity) throws IOException {

            cs.queryForCancel(jobEntity.getQueryId());

    }

    @Override
    public void updateProcessMessage(JobEntity job) throws IOException {

            HttpResult hr = cs.queryForProcess(job.getQueryId());
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
                        job.setElapsed((int) elapsed);
                        job.setRowsRead(read_rows);
                        if(job.getDoneProgress()<process){
                            job.setDoneProgress(process);
                        }
                        if (job.getRowsRead() != 0 || job.getElapsed() != 0) {
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
                logger.error("write HBase error retry count" + (3 - k) + e.toString());
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
            if (("DONE").equals(entity.getState()) && entity.getIsFailed() == 0 && entity.getIsValid() == 1) {

                mons.addSuccess();
            } else {
                mons.addFail();
            }
    }
}
