package com.mesa.reportservice.util;

import com.mesa.reportservice.bean.JobEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wk1 on 2019/5/28.
 */

@Component
@ConfigurationProperties(prefix = "globle")
public class ConfigUtil {


   // public static ExecutorService pool = Executors.newFixedThreadPool(30);
   // public static int query_log_sleep;
    //public static int loop_number;
    public static int job_thread;
    public static int ck_timeout;
    public static Map<String, JobEntity> mapresult = new ConcurrentHashMap<>();


    public void setCk_timeout(int ck_timeout) {
        ConfigUtil.ck_timeout = ck_timeout;
    }



    public void setJob_thread(int job_thread) {
        ConfigUtil.job_thread = job_thread;
    }


}
