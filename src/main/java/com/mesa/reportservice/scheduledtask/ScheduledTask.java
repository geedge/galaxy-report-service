package com.mesa.reportservice.scheduledtask;

import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.service.DataBaseBusiness;
import com.mesa.reportservice.service.HttpAPIService;
import com.mesa.reportservice.util.ClickhouseConfig;
import com.mesa.reportservice.util.ConfigUtil;
import com.mesa.reportservice.util.HbaseUtil;
import com.mesa.reportservice.util.Logs;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wk1 on 2019/5/14.
 */
@Component
@EnableScheduling
public class ScheduledTask {


    protected static ExecutorService pool = Executors.newFixedThreadPool(9);
    @Autowired
    private HttpAPIService hp;
    @Autowired
    private DataBaseBusiness hb;

   /* @Scheduled(cron = "${scan.mysql.scheduled.plan}")
    public void run() {

        System.out.print(Thread.currentThread());
        Logs.debug("jobthread=000000000000000" + ConfigUtil.job_thread);
        try {
            if (ConfigUtil.job_thread > 0) {
                List<JobEntity> joblist = hb.getJobLimitRows(ConfigUtil.job_thread);
                Logs.debug("joblist size=" + joblist.size());

                for (JobEntity jl : joblist) {
                   // Thread.sleep(2000);
                    ConfigUtil.job_thread--;
                    Logs.debug("jobthread--=" + ConfigUtil.job_thread + "---jobid=" + jl.getResult_id());
                    pool.execute(new Runnable() {
                        @Override
                        public void run() {
                            Logs.debug("threadname" + Thread.currentThread());
                            JobTask jt = new JobTask(hp, hb);
                            try {
                                jt.dotask(jl);
                            } catch (Exception e) {
                                Logs.error(e.toString());
                            }
                        }
                    });

                }
            }
        } catch (Exception e) {
            Logs.error(e.toString());
        }
    }
*/


    @Scheduled(cron = "${scan.mysql.scheduled.plan}")
    public void scanmysql() {

        try {


            List<JobEntity> joblist = hb.getJobLimitRows(ConfigUtil.job_thread);
            Logs.debug("joblist size=" + joblist.size());

            for (JobEntity jl : joblist) {
                String sql = jl.getQuery_sql().trim();
                String queryid = DigestUtils.md5Hex(jl.getResult_id() + sql);
                sql = sql.replace("$exe_time", "toDateTime('" + jl.getEnd_time().trim() + "')");
                jl.setQuery_id(queryid);
                jl.setQuery_sql(sql);
                jl.setStatus(1);
                jl.setResult("");
                jl.setException("");
                jl.setExcute_detail("");
                JobEntity jt = (JobEntity) jl.clone();
                ConfigUtil.mapresult.put(queryid, jt);

                hb.updateStatue(jl.getResult_id(), 1);

                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        JobEntity je = (JobEntity) jl.clone();
                        ExcuteTask ex = new ExcuteTask(hp);
                        ex.dotask(je);

                    }
                });
                ConfigUtil.job_thread--;


            }

        } catch (Exception e) {
            Logs.error(e.toString());
        }
    }


    @Scheduled(cron = "${scan.job.scheduled.plan}")
    public void scanmysqlresult() {

        try {


            List<JobEntity> joblist = hb.getJobForExcute();
            Logs.debug("joblist size=" + joblist.size());

            for (JobEntity jk : joblist) {
                String sql = jk.getQuery_sql().trim();
                String queryid = DigestUtils.md5Hex(jk.getResult_id() + sql);
                if (!ConfigUtil.mapresult.containsKey(queryid)) {
                    String killurl = ClickhouseConfig.getKillUrl(queryid);
                    hp.doPost(killurl);
                    updateErrorMessage(jk.getResult_id(), 0, "执行结果不存在重新执行");

                    break;
                }


                JobEntity je = (JobEntity) ConfigUtil.mapresult.get(queryid).clone();
               /* if (je == null) {
                    break;
                }*/
                if (je.getStatus() == 2 ) {
                    try {
                        je = getQueryLogMessage(je);
                        HttpResult h1 = saveToHbase(je);

                        if (h1.getCode() == 200) {
                            //Thread.sleep(5000);
                            // Logs.debug(entity.getJob_id() + entity.getStatue());

                            je.setExcute_detail("success");
                            je.setExcute_time(0);
                            je.setExcute_process(100);
                            je.setStatus(2);
                            int number = 0;
                            int z = 3;
                            do {
                                number = hb.updateProcesses(je);
                                z--;
                            }
                            while (number != 1 && z >= 0);

                        } else {
                            updateErrorMessage(je.getResult_id(), 5, "写入hbase失败");
                            Logs.error("写入hbase失败");
                        }
                    } catch (Exception e) {
                        Logs.error(e.toString());

                    } finally {
                        ConfigUtil.mapresult.remove(queryid);
                        ConfigUtil.job_thread++;
                    }


                } else if (je.getStatus() > 2) {

                    try {
                        je = getQueryLogMessage(je);
                        HttpResult h1 = saveToHbase(je);
                        if (h1.getCode() == 200) {

                            int number = 0;
                            int z = 3;
                            do {
                                number = hb.updateProcesses(je);
                                z--;
                            }
                            while (number != 1 && z >= 0);

                        } else {
                            updateErrorMessage(je.getResult_id(), 5, "错误日志写入hbase失败");
                            Logs.error("错误日志写入hbase失败");
                        }
                    } catch (Exception e) {
                        Logs.error(e.toString());

                    } finally {
                        ConfigUtil.mapresult.remove(queryid);
                        ConfigUtil.job_thread++;
                    }

                } else {
                    JobEntity jo = getProcessMessage(je);
                    if(jo.getExcute_row()!=0 || jo.getExcute_time()!=0) {
                        hb.updateProcesses(jo);
                    }
                }


            }

        } catch (Exception e) {
            Logs.error(e.toString());
            // ConfigUtil.job_thread++;

        }
    }


    public void updateErrorMessage(int id, int status, String error) {


        JobEntity js = new JobEntity();
        js.setResult_id(id);
        js.setStatus(status);
        js.setExcute_detail("任务执行中报错" + error);
        hb.updateProcesses(js);

    }


    public JobEntity getProcessMessage(JobEntity jb) {
        JobEntity job = (JobEntity) jb.clone();
        String queryProccess = ClickhouseConfig.getProcessUrl(job.getQuery_id());
        HttpResult hr = null;
        try {
            hr = hp.doPost(queryProccess);
            String rs = hr.getBody().trim();
            if (!rs.isEmpty() && !rs.equals("")) {
                //JobEntity job = new JobEntity();
                Map map = JSON.parseObject(rs);
                float elapsed = Float.parseFloat(map.get("elapsed").toString());
                long total_rows_approx = Long.parseLong(map.get("total_rows_approx").toString());
                long read_rows = Long.parseLong(map.get("read_rows").toString());
                int ftime = (int) (elapsed * total_rows_approx / read_rows);
                ftime = Math.round((ftime - elapsed) * 10 * 1000) / 10;
                if(ftime<0){
                    ftime=0;
                }
                Logs.info("total_rows_approx" + total_rows_approx + "read_rows" + read_rows + "elapsed" + elapsed + "ftime" + ftime);
                // NumberFormat numberFormat = NumberFormat.getInstance();
                //numberFormat.setMaximumFractionDigits(0);

                double persent = (read_rows * 1.00 / total_rows_approx);
                int result = (int) (persent * 100);
                job.setExcute_row(total_rows_approx);
                job.setExcute_process(result);
                //  job.setStatus(1);
                job.setExcute_time(ftime);
                job.setExcute_detail("");
                System.out.println("%+++++++++++++" + result + "%");
                System.out.println("needtime--------" + ftime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return job;

    }

    public HttpResult saveToHbase(JobEntity entity) {

        int k = 3;
        HttpResult h1 = new HttpResult();

        do {
            try {
                String hbasejson = HbaseUtil.getHbaseJson(entity.getQuery_id(), entity.getResult(), entity);
                String hbaseurl = HbaseUtil.getHbasePostUrl();
                k--;
                h1 = hp.doPost(hbaseurl, hbasejson);
                break;
            } catch (Exception e) {
                Logs.error("写入hbase报错重试次数" + (3 - k));
                k--;
                e.printStackTrace();
            }
        }
        while (k >= 0);

        //String h2 = hp.doGet("http://192.168.10.224:4444/tsg:report_result/" + queryid + "/response:result");
        //   Map<String, Object> aaa = h2.queryData("tsg:report_result",query_id);
        // System.out.print(h2);
        return h1;
    }


    public JobEntity getQueryLogMessage(JobEntity job) {


       // JobEntity job = (JobEntity) jb.clone();
    /*    job.setResult_id(resultid);
        job.setQuery_id(queryid);*/
        String finishurl = ClickhouseConfig.getFinishUrl(job.getQuery_id());
        HttpResult num = new HttpResult();
        int j = 0;
        do {
            try {
                Thread.sleep(3000);
                num = hp.doPost(finishurl);
                if (num.getCode() == 200 && !num.getBody().isEmpty() && !num.getBody().equals("")) {

                    Map mapresult = JSON.parseObject(num.getBody());
                    int type = Integer.parseInt(mapresult.get("type").toString());
                    Logs.debug("type" + type);
                    switch (type) {
                        case 2:
                            job.setStatus(type);
                            job.setExcute_detail("success");
                            job.setQuery_duration_ms(mapresult.get("query_duration_ms").toString());
                            job.setMemory_usage(mapresult.get("memory_usage").toString());
                            job.setExcute_row(Long.parseLong(mapresult.get("read_rows").toString()));
                            break;
                        case 3:
                            job.setStatus(type);
                            job.setExcute_detail("语法错误"+mapresult.get("exception").toString());
                            job.setQuery_sql(mapresult.get("query").toString());
                            job.setException(mapresult.get("exception").toString());
                            break;
                        case 4:
                            job.setStatus(type);
                            job.setExcute_detail("性能问题"+mapresult.get("exception").toString());
                            job.setQuery_sql(mapresult.get("query").toString());
                            job.setException(mapresult.get("exception").toString());
                            break;
                        default:
                            break;
                    }


                } else {
                    job.setStatus(6);
                    job.setExcute_detail("没有查到query信息");
                    job.setException("没有查到query信息");

                }
            } catch (Exception e) {
                job.setStatus(6);
                job.setException("获取querylogmessage失败");
                Logs.error("获取querylogmessage失败");
            }
            j--;
        }
        while (job.getStatus() == 6 && j > 0);
        return job;
    }


}
