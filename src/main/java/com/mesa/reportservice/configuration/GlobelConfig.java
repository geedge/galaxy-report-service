package com.mesa.reportservice.configuration;

import com.mesa.reportservice.bean.JobEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wk1 on 2019/5/28.
 */

@Component
@ConfigurationProperties(prefix = "globle")
public class GlobelConfig {

    public static int job_thread;
    public static Map<String, JobEntity> mapresult = new ConcurrentHashMap<>();
    public void setJob_thread(int job_thread) {
        GlobelConfig.job_thread = job_thread;
    }
    public final static String zkuuid = UUID.randomUUID().toString().replaceAll("-","");
}
