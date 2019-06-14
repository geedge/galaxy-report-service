package com.mesa.reportservice.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wk1 on 2019/5/28.
 */

@Component
@ConfigurationProperties(prefix = "globle")
public class ConfigUtil {


    public static ExecutorService pool = Executors.newFixedThreadPool(30);
    public static int query_log_sleep;
    public static int loop_number;
    public static int job_thread;
    public static int ck_timeout;


    public void setCk_timeout(int ck_timeout) {
        ConfigUtil.ck_timeout = ck_timeout;
    }

    public void setQuery_log_sleep(int query_log_sleep) {
        ConfigUtil.query_log_sleep = query_log_sleep;
    }

    public void setLoop_number(int loop_number) {
        ConfigUtil.loop_number = loop_number;
    }


    public void setJob_thread(int job_thread) {
        ConfigUtil.job_thread = job_thread;
    }


}
