package com.mesa.reportservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by wk1 on 2019/5/17.
 */
@Component
@ConfigurationProperties(prefix = "ck")
public class ClickhouseConfig {

    private static String gateway_ip;

    public  void setGateway_ip(String gateway_ip) {
        ClickhouseConfig.gateway_ip = gateway_ip;
    }

    public  String getGateway_ip() {
        return gateway_ip;
    }
}
