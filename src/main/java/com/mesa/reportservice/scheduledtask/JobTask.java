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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * Created by wk1 on 2019/5/31.
 */

@Component

public class JobTask {

    protected ExecutorService pool = ConfigUtil.pool;
    private HttpAPIService hp;
    private DataBaseBusiness hb;

    public JobTask(HttpAPIService hp, DataBaseBusiness hb) {

        this.hb = hb;
        this.hp = hp;
        Logs.debug("new Jobtask==========");
    }

    public HttpAPIService getHp() {
        return hp;
    }

    public void setHp(HttpAPIService hp) {
        this.hp = hp;
    }

    public DataBaseBusiness getHb() {
        return hb;
    }

    public void setHb(DataBaseBusiness hb) {
        this.hb = hb;
    }

    public void dotask(JobEntity jl) {
        int id = jl.getResult_id();
        String sql = jl.getQuery_sql().trim();
        String hbaseid = DigestUtils.md5Hex(id + sql);
        sql = sql.replace("$exe_time", "toDateTime('" + jl.getEnd_time().trim() + "')");
        String query_id = UUID.randomUUID().toString().replaceAll("-", "");
        String httpQuery = ClickhouseConfig.getJobUrl(sql, query_id);
        hb.updateStatue(id, 1);

        int a = 3;
        do {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        int i;

                        for (i = 0; i < ConfigUtil.loop_number; i++) {
                            Thread.sleep(ConfigUtil.query_log_sleep);
                            String queryProccess = ClickhouseConfig.getProcessUrl(query_id);
                            HttpResult hr = hp.doPost(queryProccess);
                            String rs = hr.getBody().trim();
                            if (!rs.isEmpty() && !rs.equals("")) {
                                //  int status = hb.getJobStatueFromId(id);
                                JobEntity en = getProcessMessage(rs, id);
                                en.setExcute_detail("");
                                if (en.getExcute_time() < (ConfigUtil.ck_timeout)) {

                                    if (en.getExcute_time() > ConfigUtil.query_log_sleep) {
                                        hb.updateProcesses(en);
                                    } else {
                                        break;
                                    }
                                } else {
                                    i = ConfigUtil.loop_number;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (i >= ConfigUtil.loop_number) {
                            String killurl = ClickhouseConfig.getKillUrl(query_id);
                            hp.doPost(killurl);
                        }
                    } catch (Exception e) {
                        Logs.error("获取进度失败" + e.toString());
                    } finally {
                        Logs.debug("thread finish--------------------------" + Thread.currentThread());
                        countDownLatch.countDown();
                    }
                }
            });

            try {
                HttpResult hr = new HttpResult();
                hr = hp.doPost(httpQuery);
                countDownLatch.await();
                JobEntity entity = getQueryLogMessage(id, query_id);

                if (hr.getCode() == 200) {

                    entity.setQuery_id(query_id);
                    HttpResult h1 = saveToHbase(hbaseid, hr.getBody(), entity);
                    if (h1.getCode() == 200) {
                        //Thread.sleep(5000);
                        // Logs.debug(entity.getJob_id() + entity.getStatue());

                        entity.setResult_id(id);
                        entity.setExcute_detail("success");
                        entity.setExcute_time(0);
                        entity.setExcute_process(100);
                        entity.setStatus(2);
                        int number = 0;
                        int z = 3;
                        do {
                            number = hb.updateProcesses(entity);
                            z--;
                        }
                        while (number != 1 && z >= 0);
                        break;
                    } else {
                        updateErrorMessage(id, 5, "写入hbase失败");
                        Logs.error("写入hbase失败");
                        break;
                    }
                } else {

                    if (hr.getBody().contains("Code: 394")) {
                        throw new IOException("queryid=" + query_id + " hbaseid=" + hbaseid + " 查询超时");
                    } else if (hr.getBody().contains("Code: 241")) {
                        throw new Exception("查询超出内存限制" + hr.getBody());
                    } else if (entity.getStatus() == 3) {
                        updateErrorMessage(id, 3, "查询语句错误" + hr.getBody());
                        break;
                    } else {
                        Logs.error("查询clickhouse失败" + hr.getBody());
                        throw new Exception("查询clickhouse失败" + hr.getBody());
                    }
                }
       /*     String h2=hp.doGet("http://192.168.10.202:4444/tsg:report_result/"+query_id+"/response:result");
            // Map<String, Object> aaa = hs.queryData("tsg:report_result",query_id);
            System.out.print(h2);*/

            } catch (InterruptedException w) {
                Logs.error(w.toString());
                break;
            } catch (IOException r) {
                a--;
                String kill = ClickhouseConfig.getKillUrl(query_id);
                try {
                    hp.doPost(kill);
                } catch (Exception er) {
                    Logs.error(er.toString());
                }
                if (a <= 0) {
                    updateErrorMessage(id, 4, "查询超时");
                    break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                Logs.error("任务执行中报错" + e.toString());
                updateErrorMessage(id, 4, e.toString());
                break;
            }
        }
        while (a > 0);
        synchronized (this) {
            ConfigUtil.job_thread++;
            Logs.debug("job_thread++++++++++++++" + ConfigUtil.job_thread);

        }
    }


    public void updateErrorMessage(int id, int status, String error) {


        JobEntity js = new JobEntity();
        js.setResult_id(id);
        js.setStatus(status);
        js.setExcute_detail("任务执行中报错" + error);
        hb.updateProcesses(js);

    }


    public JobEntity getProcessMessage(String body, int id) {
        JobEntity job = new JobEntity();
        Map map = JSON.parseObject(body);
        float elapsed = Float.parseFloat(map.get("elapsed").toString());
        long total_rows_approx = Long.parseLong(map.get("total_rows_approx").toString());
        long read_rows = Long.parseLong(map.get("read_rows").toString());
        int ftime = (int) (elapsed * total_rows_approx / read_rows);
        ftime = Math.round((ftime - elapsed) * 10 * 1000) / 10;
        Logs.info("total_rows_approx" + total_rows_approx + "read_rows" + read_rows + "elapsed" + elapsed + "ftime" + ftime);
        // NumberFormat numberFormat = NumberFormat.getInstance();
        //numberFormat.setMaximumFractionDigits(0);

        double persent = (read_rows * 1.00 / total_rows_approx);
        int result = (int) (persent * 100);
        job.setExcute_row(total_rows_approx);
        job.setExcute_process(result);
        job.setStatus(1);
        job.setExcute_time(ftime);
        job.setResult_id(id);
        System.out.println("%+++++++++++++" + result + "%");
        System.out.println("needtime--------" + ftime);
        return job;

    }

    public HttpResult saveToHbase(String hbaseid, String body, JobEntity entity) throws Exception {

        int k = 3;
        HttpResult h1 = new HttpResult();

        do {
            try {
                String hbasejson = HbaseUtil.getHbaseJson(hbaseid, body, entity);
                String hbaseurl = HbaseUtil.getHbasePostUrl();
                k--;
                h1 = hp.doPost(hbaseurl, hbasejson);
                break;
            } catch (Exception e) {
                Logs.error("写入hbase报错重试次数" + (3 - k));
                if (k < 0) {
                    throw new Exception("写入hbase报错", e);
                }
            }
        }
        while (k >= 0);
        return h1;
    }


    public JobEntity getQueryLogMessage(int id, String query_id) {


        JobEntity job = new JobEntity();
        String finishurl = ClickhouseConfig.getFinishUrl(query_id);
        HttpResult num = null;
        int j = 3;
        do {
            try {
                Thread.sleep(3000);
                num = hp.doPost(finishurl);
                if (num.getCode() == 200 && !num.getBody().isEmpty() && !num.getBody().equals("")) {

                    job.setResult_id(id);
                    Map mapresult = JSON.parseObject(num.getBody());
                    int type = Integer.parseInt(mapresult.get("type").toString());
                    String query_duration_ms = mapresult.get("query_duration_ms").toString();
                    long read_rows = Long.parseLong(mapresult.get("read_rows").toString());
                    String memory_usage = mapresult.get("memory_usage").toString();
                    Logs.debug("type" + type);
                    switch (type) {
                        case 2:
                            job.setStatus(2);
                            // job.setExcute_detail("success");
                            break;
                        case 3:
                            job.setStatus(3);
                            //job.setExcute_detail("语法错误");
                            break;
                        case 4:
                            job.setStatus(4);
                            //job.setExcute_detail("性能问题");
                            break;
                        default:
                            break;
                    }
                    job.setQuery_duration_ms(query_duration_ms);
                    job.setMemory_usage(memory_usage);
                    job.setExcute_time(0);
                    job.setExcute_process(100);
                    job.setExcute_row(read_rows);
                } else {
                    job.setStatus(5);
                    job.setExcute_detail("没有查到query信息");

                }
            } catch (Exception e) {
                job.setStatus(5);
                Logs.error("获取querylogmessage失败");
            }
            j--;
        }
        while (job.getStatus() == 5 && j >= 0);
        return job;
    }

}
