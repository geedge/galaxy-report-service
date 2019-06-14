package com.mesa.reportservice.scheduledtask;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.service.DataBaseBusiness;
import com.mesa.reportservice.service.HttpAPIService;
import com.mesa.reportservice.util.ConfigUtil;
import com.mesa.reportservice.util.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Scheduled(cron = "${scan.mysql.scheduled.plan}")
    public void run() {

        System.out.print(Thread.currentThread());
        Logs.debug("jobthread=000000000000000" + ConfigUtil.job_thread);
        try {
            if (ConfigUtil.job_thread > 0) {
                List<JobEntity> joblist = hb.getJobLimitRows(ConfigUtil.job_thread);
                Logs.debug("joblist size=" + joblist.size());

                for (JobEntity jl : joblist) {
                    Thread.sleep(2000);
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


    // @Scheduled(cron = "${scan.mysql.scheduled.plan}")
    /*public void run() {

        System.out.print(Thread.currentThread());
        List<JobEntity> joblist = hb.getJobLimitRows(1);
        for (JobEntity jl : joblist) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            int id = jl.getJob_id();
            String sqlold = jl.getQuery_sql().trim();
            String query_id = UUID.randomUUID().toString().replaceAll("-", "");
            String sql = sqlold.replaceAll(";", "");
            String hbaseid = DigestUtils.md5Hex(sqlold);

            try {

                String httpQuery = ClickhouseConfig.getJobUrl(sql, query_id);
                System.out.print(httpQuery);
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int i;
                            for (i = 0; i < ConfigUtil.loop_number; i++) {
                                Thread.sleep(ConfigUtil.query_log_sleep);
                                String queryProccess = ClickhouseConfig.getProcessUrl(query_id);
                                //System.out.println("queryProccess"+queryProccess);

                                HttpResult hr = hp.doPost(queryProccess);
                                String rs = hr.getBody().trim();
                                if (!rs.isEmpty() && !rs.equals("")) {
                                    //  int status = hb.getJobStatueFromId(id);
                                    JobEntity en = getProcessMessage(rs, id);
                                    en.setExcute_detail("");
                                    if (en.getExcute_time() < ConfigUtil.ck_timeout) {

                                        if (en.getExcute_time() > ConfigUtil.query_log_sleep) {

                                            hb.updateProcesses(en);

                                        } else {
                                            break;
                                        }
                                    } else {

                                        i = 1001;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (i >= 1000) {
                                String killurl = ClickhouseConfig.getKillUrl(query_id);
                                hp.doPost(killurl);
                            }
                        } catch (Exception e) {
                            logger.error("获取进度失败" + e.toString());
                            updateErrorMessage(id, e.toString());
                        }
                        countDownLatch.countDown();
                    }
                });


                HttpResult hr = hp.doPost(httpQuery);
                countDownLatch.await();

                if (hr.getCode() == 200) {

                        String hbasejson = HbaseUtil.getHbaseJson(hbaseid, hr.getBody());
                        String hbaseurl = HbaseUtil.getHbasePostUrl();

                        HttpResult h1 = hp.doPost(hbaseurl, hbasejson);

                        if (h1.getCode() == 200) {
                            JobEntity entity = new JobEntity();
                            int j = 3;
                            do {
                                Thread.sleep(3000);
                                entity = getQueryLogMessage(id, query_id);
                                j--;
                            }
                            while (entity.getStatue() != 2 && j > 0);
                            System.out.print(entity.getJob_id() + entity.getStatue());

                            hb.updateProcesses(entity);
                        } else {
                            updateErrorMessage(id, "写入hbase失败");
                        }

                } else {

                    if (hr.getBody().contains("cancelled")) {

                        updateErrorMessage(id, "查询超时任务已经取消");
                    }
                    else{
                        updateErrorMessage(id, "查询clickhouse失败" + hr.getBody());
                    }
                }
                String h2 = hp.doGet("http://192.168.10.202:4444/tsg:report_result/" + hbaseid + "/response:result");
                // Map<String, Object> aaa = hs.queryData("tsg:report_result",query_id);
                System.out.print("===============" + h2);
            } catch (Exception e) {

                logger.error("任务执行中报错" + e.toString());
                updateErrorMessage(id, e.toString());
                break;
            }
        }
    }*/







/*    private synchronized void updateErrorMessage(int id, String error) {


        JobEntity js = new JobEntity();
        js.setJob_id(id);
        js.setStatue(3);
        js.setExcute_detail("任务执行中报错" + error);
        hb.updateProcesses(js);

    }


    private synchronized JobEntity getProcessMessage(String body, int id) {
        JobEntity job = new JobEntity();
        Map map = JSON.parseObject(body);
        float elapsed = Float.parseFloat(map.get("elapsed").toString());
        long total_rows_approx = Long.parseLong(map.get("total_rows_approx").toString());
        long read_rows = Long.parseLong(map.get("read_rows").toString());
        int ftime = (int) (elapsed * total_rows_approx / read_rows);
        ftime = Math.round((ftime - elapsed) * 10 * 1000) / 10;
        logger.info("total_rows_approx" + total_rows_approx + "read_rows" + read_rows + "elapsed" + elapsed + "ftime" + ftime);

        double persent = (read_rows * 1.00 / total_rows_approx);
        int result = (int) (persent * 100);
        job.setExcute_row(total_rows_approx);
        job.setExcute_process(result);
        job.setStatue(1);
        job.setExcute_time(ftime);
        job.setJob_id(id);
        System.out.println("%+++++++++++++" + result + "%");
        System.out.println("needtime--------" + ftime);
        return job;

    }


    private synchronized JobEntity getQueryLogMessage(int id, String query_id) {

        JobEntity job = new JobEntity();

        String finishurl = ClickhouseConfig.getFinishUrl(query_id);
        HttpResult num = null;
        try {
            num = hp.doPost(finishurl);

            if (!num.getBody().isEmpty() && !num.getBody().equals("")) {


                job.setJob_id(id);
                Map mapresult = JSON.parseObject(num.getBody());
                int type = Integer.parseInt(mapresult.get("type").toString());
                int query_duration_ms = Integer.parseInt(mapresult.get("query_duration_ms").toString());
                long read_rows = Long.parseLong(mapresult.get("read_rows").toString());
                long memory_usage = Long.parseLong(mapresult.get("memory_usage").toString());
                String query = mapresult.get("query").toString();
                String event_time = mapresult.get("event_time").toString();
                logger.debug("type" + type);


                switch (type) {
                    case 2:
                        job.setStatue(2);
                        job.setExcute_detail("success");
                        break;
                    case 3:
                        job.setStatue(3);
                        job.setExcute_detail("语法错误");
                        break;
                    case 4:
                        job.setStatue(4);
                        job.setExcute_detail("性能问题");
                        break;

                    default:
                        break;
                }
                job.setExcute_time(0);
                job.setExcute_process(100);
                job.setExcute_row(read_rows);
            } else {

                job.setStatue(3);
                job.setExcute_detail("没有执行");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return job;
    }*/


}
