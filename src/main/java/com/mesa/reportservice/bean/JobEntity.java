package com.mesa.reportservice.bean;

import lombok.Data;

/**
 * Created by wk1 on 2019/5/20.
 */
@Data
public class JobEntity implements Cloneable {

    private String jobId;

    private String querySql;

    private String state;

    private String resultName;

    private float doneProgress;

    private Integer isFailed;

    private String resultMessage;

    private Integer elapsed;

    private long rowsRead;

    private long bytesRead;

    private long resultRows;

    private long resultBytes;

    private Integer isValid;

    private long startTime;

    private long endTime;

    private long lastUpdateTime;

    private long generatedTime;

    private String queryId;

    private Integer excuteStatus;

    private String result;

    private String memoryUsage;

    private String queryDurationMs;

    @Override
    public Object clone() {
        JobEntity o = null;
        try {
            o = (JobEntity) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return o;
    }
}
