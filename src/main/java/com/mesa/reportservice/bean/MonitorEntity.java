package com.mesa.reportservice.bean;

import java.util.Map;

/**
 * Created by wk1 on 2019/5/15.
 */
public class MonitorEntity {

    private Long queueJobNum;
    private Long excuteingJobNum;
    private Long todaySuccessJobNum;
    private Long todayErrorJobNum;

    private Map<String,JobEntity> joblist ;



    public MonitorEntity() {

    }

    public Long getQueueJobNum() {
        return queueJobNum;
    }

    public void setQueueJobNum(Long queueJobNum) {
        this.queueJobNum = queueJobNum;
    }

    public Long getExcuteingJobNum() {
        return excuteingJobNum;
    }

    public void setExcuteingJobNum(Long excuteingJobNum) {
        this.excuteingJobNum = excuteingJobNum;
    }

    public Long getTodaySuccessJobNum() {
        return todaySuccessJobNum;
    }

    public void setTodaySuccessJobNum(Long todaySuccessJobNum) {
        this.todaySuccessJobNum = todaySuccessJobNum;
    }

    public Long getTodayErrorJobNum() {
        return todayErrorJobNum;
    }

    public void setTodayErrorJobNum(Long todayErrorJobNum) {
        this.todayErrorJobNum = todayErrorJobNum;
    }

    public Map<String, JobEntity> getJoblist() {
        return joblist;
    }

    public void setJoblist(Map<String, JobEntity> joblist) {
        this.joblist = joblist;
    }
}
