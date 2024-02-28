package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;

import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
public interface HBaseService {

    Boolean put(JobEntity jobEntity);
}
