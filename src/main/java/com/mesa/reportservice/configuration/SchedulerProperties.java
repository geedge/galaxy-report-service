package com.mesa.reportservice.configuration;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.mesa.reportservice.bean.JobEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Data
@Component
@NacosConfigurationProperties(prefix = "scheduler", dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", type = ConfigType.YAML, autoRefreshed = true)
public class SchedulerProperties {

    private static int jobThread;

    private static Map<String, JobEntity> mapResult = new ConcurrentHashMap<>();

    public void setJobThread(int jobThread) {
        SchedulerProperties.jobThread = jobThread;
    }

    public final static String zkUUID = UUID.randomUUID().toString().replaceAll("-","");

    public int getJobThread() {
        return jobThread;
    }

    public Map<String, JobEntity> getMapResult() {
        return mapResult;
    }
}
