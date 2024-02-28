package com.mesa.reportservice.configuration;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Data
@Component
@NacosConfigurationProperties(prefix = "hbase", dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", type = ConfigType.YAML, autoRefreshed = true)
public class HBaseProperties {

    private String zookeeperQuorum;

    private String zookeeperPropertyClientPort;

    private String zookeeperZnodeParent;

    private String clientPause;

    private String clientRetriesNumber;

    private String rpcTimeout;

    private Integer connectPool;

    private String table;

    private Integer cellTtlD;
}
