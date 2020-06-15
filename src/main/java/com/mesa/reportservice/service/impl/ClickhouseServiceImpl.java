package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.service.ClickhouseService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

import static com.mesa.reportservice.util.GZIPUtils.compress;
import static com.mesa.reportservice.util.GZIPUtils.uncompress;

/**
 * Created by wk1 on 2019/5/15.
 */

@Service
public class ClickhouseServiceImpl implements ClickhouseService {

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    @Qualifier("RequestShortConfig")
    private RequestConfig RequestshortConfig;

    @Autowired
    @Qualifier("RequestLongConfig")
    private RequestConfig RequestLongConfig;

    /**
     * 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    @Override
    public String doGet(String url) throws Exception {
        // 声明 http get 请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        // 装载配置信息
        httpGet.setConfig(RequestLongConfig);

        // 发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpGet);

        // 判断状态码是否为200
        if (response.getStatusLine().getStatusCode() == 200) {
            // 返回响应体的内容

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }


        return null;
    }

    /**
     * 带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    @Override
    public String doGet(String url, Map<String, Object> map) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);

        if (map != null) {
            // 遍历map,拼接请求参数
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        // 调用不带参数的get请求
        return this.doGet(uriBuilder.build().toString());

    }

    /**
     * 带参数的post请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    @Override
    public HttpResult sendQueryForGet(String url) throws Exception,OutOfMemoryError{
        // 声明httpPost请求
        HttpGet httpGet = new HttpGet(url);


        // 加入配置信息
        httpGet.setConfig(RequestLongConfig);

        CloseableHttpResponse response = this.httpClient.execute(httpGet);

        HttpResult rs = new HttpResult();
        rs.setCode(response.getStatusLine().getStatusCode());
        rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
        return rs;
    }




    @Override
    public HttpResult QuerySystemForGet(String url) throws Exception {
        // 声明httpPost请求
        HttpGet httpGet = new HttpGet(url);
        // 加入配置信息
        httpGet.setConfig(RequestshortConfig);

        // 发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpGet);

        HttpResult rs = new HttpResult();
        rs.setCode(response.getStatusLine().getStatusCode());
        rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));

        //Thread.sleep(120);
        return rs;
    }


    @Override
    public HttpResult QuerySystemForDelete(String url) throws Exception {
        // 声明httpPost请求
        HttpDelete HttpDelete = new HttpDelete(url);
        // 加入配置信息
        HttpDelete.setConfig(RequestshortConfig);

        // 发起请求
        CloseableHttpResponse response = this.httpClient.execute(HttpDelete);

        HttpResult rs = new HttpResult();
        rs.setCode(response.getStatusLine().getStatusCode());
        rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));

        //Thread.sleep(120);
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
