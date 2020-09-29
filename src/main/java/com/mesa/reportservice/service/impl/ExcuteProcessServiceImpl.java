package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wk1 on 2020/1/8.
 */
@Service

public class ExcuteProcessServiceImpl implements ExcuteProcessService {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());


    @Autowired
    private ClickhouseService cs;
    @Autowired
    private MysqlService ms;
    @Autowired
    private HbaseService hs;
    @Autowired
    private MonitorService mons;

    @Override
    public void updateResultMessage(JobEntity je) {


        try {
            if (je.getIsValid() == 0) {
                je.setStatus(9);
                je.setExcuteDetail("CANCEL");
            } else {
                switch (je.getExcute_status()) {

                    case 200666:
                        Boolean isok = saveToHbase(je);
                        if (isok) {
                            je.setExcuteDetail("SUCCESS");
                            je.setExcuteProcess(100);
                            je.setStatus(2);
                            logger.info("success save to hbase query_id="+je.getQuery_id()+" resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                        } else {
                            je.setStatus(5);
                            je.setExcuteDetail("Write Data Error");
                            mons.addFail();
                            logger.error("save hbase error query_id="+je.getQuery_id()+" resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                        }
                        break;

                    case 400001:
                        je.setStatus(3);
                        je.setExcuteDetail("Param Syntax Error");
                        logger.error("Param Syntax Error query_id="+je.getQuery_id()+" resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                        break;
                    case 400010:
                        je.setStatus(3);
                        je.setExcuteDetail("SQL Syntax Error");
                        logger.error("SQL Syntax Error query_id="+je.getQuery_id()+"  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                        break;
                    case 500001:
                        je.setStatus(4);
                        je.setExcuteDetail("SQL Execution Error");
                        logger.error("SQL Execution Error query_id="+je.getQuery_id()+"  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                        break;
                    case 500010:
                        je.setStatus(6);
                        je.setExcuteDetail("Engine Statistics Error");
                        logger.error("Engine Statistics Error query_id="+je.getQuery_id()+"  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                        break;
                    case 555999:
                        je.setStatus(7);
                        je.setExcuteDetail("Unknow Error");
                        logger.error("Unknow Error query_id="+je.getQuery_id()+" resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
                        break;
                    default:
                        je.setStatus(8);
                        je.setExcuteDetail("System Error");
                        logger.error("System Error query_id="+je.getQuery_id()+" resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql());
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
            GlobelConfig.mapresult.remove(je.getQuery_id());
        }


    }

    @Override
    public void killQuery(JobEntity jobEntity) {
        String killurl = ClickhouseConfig.getKillUrl(jobEntity.getQuery_id());
        try {
            cs.QuerySystemForDelete(killurl);

        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            jobEntity.setStatus(0);
            jobEntity.setExcuteDetail("Re Execution");
            ms.updateProcesses(jobEntity);
        }
    }


    /**
     * 获取进度条信息
     */
    @Override
    public void updateProcessMessage(JobEntity job) {
        String queryProccess = ClickhouseConfig.getProcessUrl(job.getQuery_id());
        HttpResult hr = null;
        try {
            hr = cs.QuerySystemForGet(queryProccess);
            String rs = hr.getBody().trim();
            Map data = JSON.parseObject(rs);

            if (!rs.isEmpty() && !rs.equals("")) {
                List listdata = (List) data.get("data");
                if (null != listdata && listdata.size() > 0) {
                    Map map = JSON.parseObject(JSON.toJSONString(listdata.get(0)));
                    long total_rows_approx = Long.parseLong(map.get("total_rows_approx").toString());
                    long read_rows = Long.parseLong(map.get("read_rows").toString());
                    float elapsed = Float.parseFloat(map.get("elapsed").toString()) * 1000;
                    double persent = (read_rows * 1.00 / total_rows_approx);
                    int result = (int) (persent * 100);
                    if (result > 98) {
                        result = 98;
                    }
                    job.setExcuteTime((int) elapsed);
                    job.setExcuteRow(total_rows_approx);
                    job.setExcuteProcess(result);
                    if (job.getExcuteRow() != 0 || job.getExcuteTime() != 0) {
                        ms.updateProcesses(job);
                    }
                } else {
                    logger.error("responsedata is null");
                }
            } else {
                logger.error("responsebody is null");
            }
        } catch (Exception e) {
            logger.error(e.toString());
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

        try {
            if (entity.getStatus() == 2) {

                mons.addSuccess();
            } else {
                mons.addFail();
            }
        } catch (Exception e) {
            logger.error("监控结果失败" + e.toString());
        }
    }
}
