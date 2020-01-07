package com.mesa.reportservice.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by wk1 on 2020/1/6.
 */
@ConfigurationProperties(prefix = "zookeeper")
public class ZkProperties{

    private int open;

    private int retryCount;

    private int elapsedTimeMs;

    private String connectString;

    private int sessionTimeoutMs;

    private int connectionTimeoutMs;

    private String nameSpace;


    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    public String getConnectString() {
        return connectString;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void setElapsedTimeMs(int elapsedTimeMs) {
        this.elapsedTimeMs = elapsedTimeMs;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
}
