package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.HttpResult;

import java.util.Map;

/**
 * Created by wk1 on 2020/1/2.
 */
public interface ClickhouseService {

    String doGet(String url) throws Exception;


    String doGet(String url, Map<String, Object> map) throws Exception;


    HttpResult sendQueryForPost(String url) throws Exception;


    HttpResult QuerySystemForPost(String url) throws Exception;

}
