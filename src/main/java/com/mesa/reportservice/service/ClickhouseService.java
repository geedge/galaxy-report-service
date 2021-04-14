package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.HttpResult;

import java.io.IOException;

/**
 * Created by wk1 on 2020/1/2.
 */
public interface ClickhouseService {

    String  getQueryId(String resultId,String query) ;


    HttpResult queryForExcute(String resultId,String query) throws IOException;


    HttpResult queryForProcess(String queryId) throws IOException;


    HttpResult queryForCancel(String queryId) throws IOException;



}
