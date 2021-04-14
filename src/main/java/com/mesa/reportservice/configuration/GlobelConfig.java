package com.mesa.reportservice.configuration;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
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
@NacosConfigurationProperties(prefix = "globle", dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", type = ConfigType.YAML, autoRefreshed = true)

public class GlobelConfig {

    private static int job_thread;
    private static Map<String, JobEntity> mapresult = new ConcurrentHashMap<>();
    public void setJob_thread(int job_thread) {
        this.job_thread = job_thread;
    }
    public final static String zkuuid = UUID.randomUUID().toString().replaceAll("-","");

    public int getJob_thread() {
        return job_thread;
    }

    public Map<String, JobEntity> getMapresult() {
        return mapresult;
    }
}
