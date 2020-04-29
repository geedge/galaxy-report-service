package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mesa.reportservice.service.FormatService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by wk1 on 2020/4/15.
 */
@Service
public class FormatServiceImpl implements FormatService {

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig config;

    @Override
    public String doFormat(String url) throws Exception {

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        // 装载配置信息
        httpGet.setConfig(config);
        CloseableHttpResponse response = this.httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            // 返回响应体的内容

            JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            String responseMessage = jsonObject.getString("message");
            return responseMessage.trim();
        }
        else{
            throw new Exception("Formate sql error url = "+url);
        }

    }


}
