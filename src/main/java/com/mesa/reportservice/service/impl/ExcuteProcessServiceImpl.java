package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.service.ClickhouseService;
import com.mesa.reportservice.service.ExcuteProcessService;
import com.mesa.reportservice.service.HbaseService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.util.Logs;
import com.mesa.reportservice.util.StringUtil;
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
    public void getTaskProcess(JobEntity jobEntity) {

        try {
            String sql = jobEntity.getQuerySql().trim();
            sql = sql.replace("$exe_time", "toDateTime('" + jobEntity.getIssuedTime().trim() + "')");
            sql = sql.replace("$start_time", "toDateTime('" + jobEntity.getStartTime().trim() + "')");
            sql = sql.replace("$end_time", "toDateTime('" + jobEntity.getEndTime().trim() + "')");
            //job.setFormatSql(sql);
            String queryid = DigestUtils.md5Hex(jobEntity.getResultId() + sql);
            jobEntity.setQuery_id(queryid);
            //程序异常停止重启后kill正在进行的查询，更新数据库状态重新执行
            if (!GlobelConfig.mapresult.containsKey(queryid)) {
                String killurl = ClickhouseConfig.getKillUrl(queryid);
                try {
                    Logs.info("startkill++++++" + queryid);
                    cs.QuerySystemForDelete(killurl);
                    Logs.info("endkill=======" + queryid);

                } catch (Exception e) {
                    Logs.error(e.toString());
                } finally {
                    updateErrorMessage(jobEntity.getResultId(), 0, "Re Execution");
                }
            } else {
                if (jobEntity.getIsValid() == 0) {
                    String killurl = ClickhouseConfig.getKillUrl(queryid);
                    try {
                        Logs.info("startkill++++++" + queryid);

                        cs.QuerySystemForDelete(killurl);
                        Logs.info("endkill=======" + queryid);
                    } catch (Exception e) {
                        Logs.error(e.toString());
                    } finally {
                        //updateErrorMessage(jobEntity.getResultId(), 7, "Re Execution");
                        GlobelConfig.mapresult.get(queryid).setIsValid(0);
                        Logs.error("user cancel   resultid =" + GlobelConfig.mapresult.get(queryid).getResultId() + " excutesql=" + GlobelConfig.mapresult.get(queryid).getQuerySql()  );
                    }
                }
                JobEntity je = GlobelConfig.mapresult.get(queryid);
                Logs.info("startget++++++" + je.getQuery_id());

                if (je.getStatus() == 1) {
                    je = getProcessMessage(je);
                    if (je.getExcuteRow() != 0 || je.getExcuteTime() != 0) {
                        ms.updateProcesses(je);
                    }
                } else {
                    try {
                        if (je.getIsValid() == 0) {

                            updateErrorMessage(je.getResultId(), 9, "CANCEL");
                            //   Logs.error("excute error  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() + " formatsql=" + je.getFormatSql());

                        } else {
                            switch (je.getExcute_status()) {

                                case 200666:
                                   // je = getQueryLogMessage(je);
                                    Boolean isok = saveToHbase(je);
                                    if (isok) {
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
                                        Logs.info("success save to hbase resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() );
                                    } else {
                                        updateErrorMessage(je.getResultId(), 5, "Write Data Error");
                                        Logs.error("save hbase error resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() );
                                    }
                                    break;

                                case 400010:
                                    updateErrorMessage(je.getResultId(), 3, "SQL Syntax Error");
                                    Logs.error("SQL Syntax Error  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() );
                                    break;
                                case 500001:
                                    updateErrorMessage(je.getResultId(), 4, "SQL Execution Error");
                                    Logs.error("SQL Execution Error  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() );
                                    break;
                                case 500010:
                                    updateErrorMessage(je.getResultId(), 6, "Engine Statistics Error");
                                    Logs.error("Engine Statistics Error  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() );
                                    break;
                                case 555999:
                                    updateErrorMessage(je.getResultId(), 7, "unknow error");
                                    Logs.error("unknow error  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() );
                                    break;
                                default:
                                   // je = getQueryLogMessage(je);
                                    updateErrorMessage(je.getResultId(), 8, je.getExcuteDetail());
                                    Logs.error("unknow error  resultid =" + je.getResultId() + " excutesql=" + je.getQuerySql() );
                            }
                        }
                    } catch (Exception e) {
                        // updateErrorMessage(je.getResultId(), 6, "Result Write To Database Error");
                        Logs.error("save database error queryid=" + je.getResultId() + e.toString());

                    } finally {
                        GlobelConfig.mapresult.remove(je.getQuery_id());
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
            hr = cs.QuerySystemForGet(queryProccess);
            String rs = hr.getBody().trim();
            if (!rs.isEmpty() && !rs.equals("")) {
                //JobEntity job = new JobEntity();
                Map data = JSON.parseObject(rs);
                List listdata = (List) data.get("data");
                Map map = JSON.parseObject(JSON.toJSONString(listdata.get(0)));
                float elapsed = Float.parseFloat(map.get("elapsed").toString());
                long total_rows_approx = Long.parseLong(map.get("total_rows_approx").toString());
                long read_rows = Long.parseLong(map.get("read_rows").toString());
                int ftime = (int) (elapsed * total_rows_approx / read_rows);
                ftime = Math.round((ftime - elapsed) * 10 * 1000) / 10;
                if (ftime <= 0) {
                    ftime = 1;
                }
                //Logs.info("total_rows_approx" + total_rows_approx + "read_rows" + read_rows + "elapsed" + elapsed + "ftime" + ftime);
                double persent = (read_rows * 1.00 / total_rows_approx);
                int result = (int) (persent * 100);
                if(result>98){
                    result =98;
                }
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
    public Boolean saveToHbase(JobEntity entity) {

        int k = 3;
        Boolean isok = false;
        do {
            try {
                k--;
                isok = hs.put(entity);
                break;
            } catch (Exception e) {
                Logs.error("写入hbase报错重试次数" + (3 - k) + e.toString());
                k--;
            }
        }
        while (k >= 0);
        return isok;
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
                    num = cs.QuerySystemForGet(finishurl);
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
                                job.setResultRows(Integer.valueOf(mapresult.get("result_rows").toString()));
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
                        job.setExcuteDetail("Other ERROR ");

                    }
                } catch (Exception e) {
                    job.setStatus(6);
                    job.setExcuteDetail("Other ERROR: " + e.toString());
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
