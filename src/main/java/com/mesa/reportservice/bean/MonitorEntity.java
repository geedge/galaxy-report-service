package com.mesa.reportservice.bean;

import lombok.Data;

import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Data
public class MonitorEntity {

    private Long queueJobNum;

    private Long executingJobNum;

    private Long todaySuccessJobNum;

    private Long todayErrorJobNum;

    private Map<String,JobEntity> jobList ;

    private String status;

    public MonitorEntity() {
    }
}
