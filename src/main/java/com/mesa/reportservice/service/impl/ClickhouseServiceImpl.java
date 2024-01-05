package com.mesa.reportservice.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.service.ClickhouseService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by wk1 on 2019/5/15.
 */

@Service
public class ClickhouseServiceImpl implements ClickhouseService {

    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private ClickhouseConfig clickhouseConfig;

    @Autowired
    @Qualifier("RequestShortConfig")
    private RequestConfig RequestshortConfig;

    @Autowired
    @Qualifier("RequestLongConfig")
    private RequestConfig RequestLongConfig;
    Log logger = Log.get();


    @Override
    public String getQueryId(String query) {

        CloseableHttpResponse response = null;
        String query_id = "";
        try {
            String url = URLUtil.normalize(clickhouseConfig.getGateway_ip().trim() + "/v1/sql/query/query_id?query=");
            String sql = null;
            sql = URLEncoder.encode(query, "utf8").replaceAll("\\+", "%20");
            url = url + sql;
            HttpPost httpPost = new HttpPost(url);
            // 加入配置信息
            httpPost.setConfig(RequestshortConfig);
            response = this.httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException();
            } else {
                Map data = JSON.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                List listdata = (List) data.get("data");
                Map map = JSON.parseObject(JSON.toJSONString(listdata.get(0)));
                query_id = map.get("queryId").toString();

            }
        } catch (IOException e) {
            logger.error(e.toString());

        } finally {
            IoUtil.close(response);
        }
        return query_id;
    }

    /**
     * 带参数的post请求
     *
     * @return
     * @throws Exception
     */
    @Override
    public HttpResult queryForExcute(String query) throws UnsupportedEncodingException {
        // 声明httpPost请求
        CloseableHttpResponse response = null;
        // 发起请求
        HttpResult rs = null;
        try {
            String url = URLUtil.normalize(clickhouseConfig.getGateway_ip().trim() + "/sql?rawSQL=");
            query = URLEncoder.encode(query, "utf8").replaceAll("\\+", "%20");
            String jobsql = url + query;
            HttpGet httpGet = new HttpGet(jobsql);
            // 加入配置信息
            httpGet.setConfig(RequestLongConfig);


            response = this.httpClient.execute(httpGet);
            rs = new HttpResult();
            rs.setCode(response.getStatusLine().getStatusCode());
            rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));

        } catch (IOException e) {
            logger.error(e.toString());

        } finally {
            IoUtil.close(response);
        }
        return rs;
    }


    @Override
    public HttpResult queryForProcess(String queryId) {
        // 声明httpPost请求
        CloseableHttpResponse response = null;
        // 发起请求
        HttpResult rs = null;
        try {
            String url = URLUtil.normalize(clickhouseConfig.getGateway_ip().trim() + "/v1/sql/query/" + queryId + "/progress");

            HttpGet httpGet = new HttpGet(url);
            // 加入配置信息
            httpGet.setConfig(RequestshortConfig);


            response = this.httpClient.execute(httpGet);
            rs = new HttpResult();
            rs.setCode(response.getStatusLine().getStatusCode());
            rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            IoUtil.close(response);
        }
        return rs;
    }


    @Override
    public HttpResult queryForCancel(String queryId) {

        CloseableHttpResponse response = null;
        HttpResult rs = null;
        try {
            String url = URLUtil.normalize(clickhouseConfig.getGateway_ip().trim() + "/v1/sql/query/" + queryId);
            // 声明httpPost请求
            HttpDelete HttpDelete = new HttpDelete(url);
            // 加入配置信息
            HttpDelete.setConfig(RequestshortConfig);

            // 发起请求

            response = this.httpClient.execute(HttpDelete);
            rs = new HttpResult();
            rs.setCode(response.getStatusLine().getStatusCode());
            rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            IoUtil.close(response);
        }
        return rs;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public RequestConfig getRequestshortConfig() {
        return RequestshortConfig;
    }

    public void setRequestshortConfig(RequestConfig requestshortConfig) {
        RequestshortConfig = requestshortConfig;
    }

    public RequestConfig getRequestLongConfig() {
        return RequestLongConfig;
    }

    public void setRequestLongConfig(RequestConfig requestLongConfig) {
        RequestLongConfig = requestLongConfig;
    }

    /**
     * 不带参数post请求
     *
     * @param url
     * @return
     * @throws Exception
     */

}
