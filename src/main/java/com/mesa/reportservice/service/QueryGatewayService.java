package com.mesa.reportservice.service;

import com.mesa.reportservice.bean.HttpResult;

import java.io.IOException;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
public interface QueryGatewayService {

    String  getQueryId(String query) ;

    HttpResult queryJob(String query) throws IOException;

    HttpResult queryJobProcess(String queryId);

    HttpResult deleteJob(String queryId);
}
