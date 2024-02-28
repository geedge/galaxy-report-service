package com.mesa.reportservice.configuration;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Component
@NacosConfigurationProperties(prefix = "query-gateway", dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", type = ConfigType.YAML, autoRefreshed = true)
public class QueryGatewayProperties {

    private static String gatewayIp;

    public  void setGatewayIp(String gatewayIp) {
        QueryGatewayProperties.gatewayIp = gatewayIp;
    }

    public  String getGatewayIp() {
        return gatewayIp;
    }
}
