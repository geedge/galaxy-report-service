package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.bean.JobEntity;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;

import java.util.Map;

/**
 * Created by wk1 on 2020/1/2.
 */
public interface HbaseService {


    Boolean put(JobEntity jobEntity) throws Exception;

}
