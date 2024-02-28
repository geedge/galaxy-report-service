package com.mesa.reportservice.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.configuration.QueryGatewayProperties;
import com.mesa.reportservice.service.QueryGatewayService;
import com.mesa.reportservice.util.Constant;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class QueryGatewayServiceImpl implements QueryGatewayService {

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private QueryGatewayProperties queryGatewayProperties;

    @Autowired
    @Qualifier("RequestShortConfig")
    private RequestConfig RequestShortConfig;

    @Autowired
    @Qualifier("RequestLongConfig")
    private RequestConfig RequestLongConfig;

    private static final Log logger = LogFactory.get();


    @Override
    public String getQueryId(String query) {
        CloseableHttpResponse response = null;
        String queryId = "";
        try {
            String url = URLUtil.normalize(MessageFormat.format(Constant.URL_GET_QUERY_ID,queryGatewayProperties.getGatewayIp().trim()));
            String sql = null;
            sql = URLEncoder.encode(query, "utf8").replaceAll("\\+", "%20");
            url = url + sql;
            HttpPost httpPost = new HttpPost(url);
            // 加入配置信息
            httpPost.setConfig(RequestShortConfig);
            response = this.httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                throw new IOException();
            } else {
                Map data = JSON.parseObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
                List listData = (List) data.get("data");
                Map map = JSON.parseObject(JSON.toJSONString(listData.get(0)));
                queryId = map.get("queryId").toString();
            }
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            IoUtil.close(response);
        }
        return queryId;
    }

    /**
     * 带参数的get请求
     *
     * @return
     * @throws Exception
     */
    @Override
    public HttpResult queryJob(String query) throws UnsupportedEncodingException {
        // 声明httpGet请求
        CloseableHttpResponse response = null;
        // 发起请求
        HttpResult httpResult = null;
        try {
            String url = URLUtil.normalize(MessageFormat.format(Constant.URL_QUERY_FOR_EXECUTE,queryGatewayProperties.getGatewayIp().trim()));
            query = URLEncoder.encode(query, "utf8").replaceAll("\\+", "%20");
            String jobSql = url + query;
            HttpGet httpGet = new HttpGet(jobSql);
            // 加入配置信息
            httpGet.setConfig(RequestLongConfig);
            response = this.httpClient.execute(httpGet);
            httpResult = new HttpResult();
            httpResult.setCode(response.getStatusLine().getStatusCode());
            httpResult.setBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            IoUtil.close(response);
        }
        return httpResult;
    }


    @Override
    public HttpResult queryJobProcess(String queryId) {
        // 声明httpGet请求
        CloseableHttpResponse response = null;
        // 发起请求
        HttpResult httpResult = null;
        try {
            String url = URLUtil.normalize(MessageFormat.format(Constant.URL_QUERY_FOR_PROCESS,queryGatewayProperties.getGatewayIp().trim(),queryId));
            HttpGet httpGet = new HttpGet(url);
            // 加入配置信息
            httpGet.setConfig(RequestShortConfig);
            response = this.httpClient.execute(httpGet);
            httpResult = new HttpResult();
            httpResult.setCode(response.getStatusLine().getStatusCode());
            httpResult.setBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            IoUtil.close(response);
        }
        return httpResult;
    }


    @Override
    public HttpResult deleteJob(String queryId) {
        CloseableHttpResponse response = null;
        HttpResult httpResult = null;
        try {
            String url = URLUtil.normalize(MessageFormat.format(Constant.URL_QUERY_FOR_CANCEL,queryGatewayProperties.getGatewayIp().trim(),queryId));
            // 声明httpDelete请求
            HttpDelete HttpDelete = new HttpDelete(url);
            // 加入配置信息
            HttpDelete.setConfig(RequestShortConfig);
            // 发起请求
            response = this.httpClient.execute(HttpDelete);
            httpResult = new HttpResult();
            httpResult.setCode(response.getStatusLine().getStatusCode());
            httpResult.setBody(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            IoUtil.close(response);
        }
        return httpResult;
    }
}
