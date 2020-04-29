package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.configuration.HbaseConfig;
import com.mesa.reportservice.service.ClickhouseService;
import com.mesa.reportservice.service.ExcuteProcessService;
import com.mesa.reportservice.service.HbaseService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.util.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wk1 on 2020/1/8.
 */
@Service

public class ExcuteProcessServiceImpl implements ExcuteProcessService {


    @Autowired
    private ClickhouseService cs;
    @Autowired
    private MysqlService ms;
    @Autowired
    private HbaseService hs;

    @Override
    public void getTaskProcess(List<JobEntity> joblist) {

        try {
            for (JobEntity jk : joblist) {
                String sql = jk.getQuerySql().trim();
                String queryid = DigestUtils.md5Hex(jk.getResultId() + sql);
                //程序异常停止重启后kill正在进行的查询，更新数据库状态重新执行
                if (!GlobelConfig.mapresult.containsKey(queryid)) {
                   String killurl = ClickhouseConfig.getKillUrl(queryid);
                    try {
                        Logs.info("startkill++++++"+queryid);

                        cs.QuerySystemForPost(killurl);
                        Logs.info("endkill======="+queryid);
                    } catch (Exception e) {
                        Logs.error(e.toString());
                    }
                    updateErrorMessage(jk.getResultId(), 0, "Re Execution");
                }
                else {
                    JobEntity je = (JobEntity) GlobelConfig.mapresult.get(queryid).clone();
                    Logs.info("startget++++++" + queryid);

                    switch (je.getStatus()) {
                        case 1:
                            je = getProcessMessage(je);

                            if (je.getExcuteRow() != 0 || je.getExcuteTime() != 0) {
                                ms.updateProcesses(je);
                            }
                            break;
                        case 2:
                            try {
                                je = getQueryLogMessage(je);
                                HttpResult hr = saveToHbase(je);

                                if (hr.getCode() == 200) {

                                    je.setExcuteDetail("SUCCESS");
                                    je.setExcuteTime(0);
                                    je.setExcuteProcess(100);
                                    je.setStatus(2);
                                    int number = 0;
                                    int z = 3;
                                    do {
                                        number = ms.updateProcesses(je);
                                        z--;
                                    }
                                    while (number != 1 && z >= 0);
                                    Logs.info("success save to hbase queryid=" + je.getQuery_id() + " sql=" + je.getQuerySql());
                                } else {
                                    updateErrorMessage(je.getResultId(), 5, "Write To HBase Error");
                                    Logs.error("save hbase error" + hr.getBody());
                                }
                            } catch (Exception e) {
                                updateErrorMessage(je.getResultId(), 6, "Result Write To Mysql Error");
                                Logs.error("save hbase error queryid=" + je.getResultId() + e.toString());

                            } finally {
                                GlobelConfig.mapresult.remove(queryid);
                            }
                            break;

                        default:
                            try {
                                je = getQueryLogMessage(je);
                                updateErrorMessage(je.getResultId(), je.getStatus(), je.getExcuteDetail());
                            } catch (Exception e) {
                                Logs.error(e.toString());
                            } finally {
                                GlobelConfig.mapresult.remove(queryid);
                            }
                    }
                }
            }
        } catch (Exception e) {
            Logs.error(e.toString());

        }
    }


    public void updateErrorMessage(int id, int status, String error) {


        JobEntity js = new JobEntity();

        try {
            js.setResultId(id);
            js.setStatus(status);
            js.setExcuteDetail(StringUtil.bSubstring(error, 1000));
        } catch (Exception e) {
            Logs.error("updateErrorMessage Error " + e.toString());
            js.setExcuteDetail("updateErrorMessage Error");
        }
        ms.updateProcesses(js);

    }

    /**
     * 获取进度条信息
     */
    public JobEntity getProcessMessage(JobEntity job) {
        // JobEntity job = (JobEntity) jb.clone();
        String queryProccess = ClickhouseConfig.getProcessUrl(job.getQuery_id());
        HttpResult hr = null;
        try {
            hr = cs.sendQueryForPost(queryProccess);
            String rs = hr.getBody().trim();
            if (!rs.isEmpty() && !rs.equals("")) {
                //JobEntity job = new JobEntity();
                Map map = JSON.parseObject(rs);
                float elapsed = Float.parseFloat(map.get("elapsed").toString());
                long total_rows_approx = Long.parseLong(map.get("total_rows_approx").toString());
                long read_rows = Long.parseLong(map.get("read_rows").toString());
                int ftime = (int) (elapsed * total_rows_approx / read_rows);
                ftime = Math.round((ftime - elapsed) * 10 * 1000) / 10;
                if (ftime < 0) {
                    ftime = 0;
                }
                //Logs.info("total_rows_approx" + total_rows_approx + "read_rows" + read_rows + "elapsed" + elapsed + "ftime" + ftime);
                double persent = (read_rows * 1.00 / total_rows_approx);
                int result = (int) (persent * 100);
                job.setExcuteRow(total_rows_approx);
                job.setExcuteProcess(result);
                job.setExcuteTime(ftime);

            }
        } catch (Exception e) {
            Logs.error(e.toString());
        }
        return job;
    }

    /**
     * 结果存入hbase
     */
    public HttpResult saveToHbase(JobEntity entity) {

        int k = 3;
        HttpResult hr = new HttpResult();

        do {
            try {
                String hbasejson = HbaseConfig.getHbaseJson(entity.getQuery_id(), entity.getResult(), entity);
                String hbaseurl = HbaseConfig.getHbasePostUrl();
                k--;
                hr = hs.doPost(hbaseurl, hbasejson);
                break;
            } catch (Exception e) {
                Logs.error("写入hbase报错重试次数" + (3 - k) + e.toString());
                k--;
            }
        }
        while (k >= 0);
        return hr;
    }

    /**
     * 获取查询log中的信息
     */
    public JobEntity getQueryLogMessage(JobEntity job) {

        if (job.getStatus() != 4) {
            String finishurl = ClickhouseConfig.getFinishUrl(job.getQuery_id());
            HttpResult num = new HttpResult();
            int j = 0;
            do {
                try {
                    //ck默认最慢同步时间7500ms，循环三次
                    Thread.sleep(3000);
                    num = cs.sendQueryForPost(finishurl);
                    if (num.getCode() == 200 && !num.getBody().isEmpty() && !num.getBody().equals("")) {

                        Map mapresult = JSON.parseObject(num.getBody());
                        int type = Integer.parseInt(mapresult.get("type").toString());
                        Logs.debug("type" + type);
                        switch (type) {
                            case 2:
                                job.setStatus(type);
                                job.setExcuteDetail("SUCCESS");
                                job.setQuery_duration_ms(mapresult.get("query_duration_ms").toString());
                                job.setMemory_usage(mapresult.get("memory_usage").toString());
                                job.setExcuteRow(Long.parseLong(mapresult.get("read_rows").toString()));
                                break;
                            case 3:
                                job.setStatus(type);
                                job.setExcuteDetail("SQL Syntax Error:" + mapresult.get("exception").toString());
                                // job.setQuerySql(mapresult.get("query").toString());
                                break;
                            case 4:
                                job.setStatus(type);
                                job.setExcuteDetail("SQL Execution Error:" + mapresult.get("exception").toString());
                                //  job.setQuerySql(mapresult.get("query").toString());
                                break;
                            default:
                                break;
                        }
                    } else {
                        job.setStatus(6);
                        if(job.getExcuteDetail().equals("")){
                            job.setExcuteDetail("Other ERROR ");
                        }

                    }
                } catch (Exception e) {
                    job.setStatus(6);
                    job.setExcuteDetail("Other ERROR: "+e.toString());
                    Logs.error("获取querylogmessage失败");
                }
                j--;
            }
            while (job.getStatus() == 6 && j > 0);
            return job;
        }

        return job;
    }
}
