package com.mesa.reportservice.controller;

import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.configuration.HttpClientPool;
import com.mesa.reportservice.service.ExcuteProcessService;
import com.mesa.reportservice.service.ExcuteService;
import com.mesa.reportservice.service.MysqlService;
import com.mesa.reportservice.service.ZkService;
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
 * @author wk1
 * @date 2020/1/8
 */
@Component
@EnableScheduling
public class ScheduledResultController {

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    protected static ExecutorService pool = Executors.newFixedThreadPool(30);


    @Autowired
    private MysqlService ms;
    @Autowired
    private ExcuteService es;
    @Autowired
    private ExcuteProcessService eps;
    @Autowired
    private ZkService zs;


    @Scheduled(cron = "${scan.result.scheduled.plan}")
    public void getExcuteResult() {
        try {
            if (zs.isMaster()) {
                logger.info("开始查看结果");
                //先查询数据库是否有异常状态任务，killquery
                List<JobEntity> joblist = ms.getJobForExcute();
                for (JobEntity jobEntity : joblist) {
                    String sql = jobEntity.getQuerySql().trim();
                    sql = sql.replace("$exe_time", "toDateTime('" + jobEntity.getIssuedTime().trim() + "')");
                    //sql = sql.replace("$start_time", "toDateTime('" + jobEntity.getStartTime().trim() + "')");
                    //sql = sql.replace("$end_time", "toDateTime('" + jobEntity.getEndTime().trim() + "')");
                    String queryid = DigestUtils.md5Hex(jobEntity.getResultId() + sql);
                    jobEntity.setQuery_id(queryid);

                    if (jobEntity.getIsValid() == 0) {
                        eps.killQuery(jobEntity);
                        GlobelConfig.mapresult.get(jobEntity.getQuery_id()).setIsValid(0);
                    } else if (!GlobelConfig.mapresult.containsKey(jobEntity.getQuery_id())) {
                        eps.killQuery(jobEntity);
                    }

                }
                //遍历内存中的任务对状态1的更新进度，其他更新数据库的状态
                for (Map.Entry<String, JobEntity> entry : GlobelConfig.mapresult.entrySet()) {
                    logger.info("key = " + entry.getKey() + ", value = " + entry.getValue().getStatus());
                    long currentTime = System.currentTimeMillis();
                    long excutetime =currentTime-entry.getValue().getBeginTime();
                    logger.info("excute time="+excutetime+"ttl_time="+HttpClientPool.socketTimeout);
                    if (entry.getValue().getStatus()==1 && excutetime> HttpClientPool.socketTimeout+1){
                        entry.getValue().setStatus(2);
                        entry.getValue().setExcute_status(500001);
                        eps.killQuery(entry.getValue());
                        eps.updateResultMessage(entry.getValue());
                    }
                    else{
                        if (entry.getValue().getStatus() == 1) {
                            eps.updateProcessMessage(entry.getValue());
                        }
                        else {
                            eps.updateResultMessage(entry.getValue());
                        }
                    }
                }
                    int rows = GlobelConfig.job_thread - GlobelConfig.mapresult.size();
                    for (Map.Entry<String, JobEntity> entry : GlobelConfig.mapresult.entrySet()) {
                        logger.info("----key = " + entry.getKey() + ", value = " + entry.getValue().getStatus());
                        System.out.print("----key = " + entry.getKey() + ", value = " + entry.getValue().getStatus());
                    }
                    if (rows > 0) {
                        List<JobEntity> jobs = ms.getJobTask(rows);
                        for (JobEntity job : jobs) {
                            logger.info("开始执行任务");
                            long begintime = System.currentTimeMillis();
                            job.setBeginTime(begintime);
                            pool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    es.excuteCkTask(job);
                                }
                            });
                        }
                    } else {
                        logger.info("无待执行任务");
                    }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}
