package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.HttpResult;

import java.util.Map;

/**
 * Created by wk1 on 2020/1/2.
 */
public interface ClickhouseService {

    String  getQueryId(String resultId,String query) throws Exception;


    HttpResult queryForExcute(String resultId,String query) throws Exception;


    HttpResult queryForProcess(String queryId) throws Exception;


    HttpResult queryForCancel(String queryId) throws Exception;



}
