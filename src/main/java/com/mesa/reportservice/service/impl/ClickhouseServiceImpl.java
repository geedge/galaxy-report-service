package com.mesa.reportservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.configuration.ClickhouseConfig;
import com.mesa.reportservice.service.ClickhouseService;
import com.mesa.reportservice.util.Logs;
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
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());


    @Override
    public String getQueryId(String resultId, String query) throws Exception {


        String url = "http://" + clickhouseConfig.getGateway_ip().trim() + "/sys/engine/queryIds?resultId=" + resultId + "&query=";
        String sql = URLEncoder.encode(query, "utf8").replaceAll("\\+", "%20");
        url = url+sql;
        HttpGet httpGet = new HttpGet(url);
        // 加入配置信息
        httpGet.setConfig(RequestshortConfig);
        try(CloseableHttpResponse response = this.httpClient.execute(httpGet) ){
            if(response.getStatusLine().getStatusCode()!=200){
                throw new Exception();
            }
            else {
                Map data = JSON.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                List listdata = (List) data.get("data");
                Map map = JSON.parseObject(JSON.toJSONString(listdata.get(0)));
                String  query_id = map.get("queryId").toString();
                return query_id;

            }
        }catch (Exception e){
            logger.error(e.toString());
            throw new Exception();
        }
    }

    /**
     * 带参数的post请求
     *
     * @return
     * @throws Exception
     */
    @Override
    public HttpResult queryForExcute(String resultId, String query) throws Exception,OutOfMemoryError{
        // 声明httpPost请求

        String url = "http://" + clickhouseConfig.getGateway_ip().trim() + "/?option=long-term&resultId=" + resultId + "&query=";
        query = URLEncoder.encode(query , "utf8").replaceAll("\\+", "%20");
        String jobsql = url + query;
        HttpGet httpGet = new HttpGet(jobsql);
        // 加入配置信息
        httpGet.setConfig(RequestLongConfig);

        try(CloseableHttpResponse response = this.httpClient.execute(httpGet) ){
            HttpResult rs = new HttpResult();
            rs.setCode(response.getStatusLine().getStatusCode());
            rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
            return rs;

        }catch (Exception e){
            logger.error(e.toString());
            throw new Exception();
        }

    }




    @Override
    public HttpResult queryForProcess(String queryId) throws Exception {
        // 声明httpPost请求
        String url = "http://" + clickhouseConfig.getGateway_ip().trim() + "/sys/engine/processes/"+queryId;

        HttpGet httpGet = new HttpGet(url);
        // 加入配置信息
        httpGet.setConfig(RequestshortConfig);

        // 发起请求
        try(CloseableHttpResponse response = this.httpClient.execute(httpGet) ){

            HttpResult rs = new HttpResult();
            rs.setCode(response.getStatusLine().getStatusCode());
            rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));

            //Thread.sleep(120);
            return rs;

    }catch (Exception e){
        logger.error(e.toString());
        throw new Exception();
    }
    }


    @Override
    public HttpResult queryForCancel(String queryId) throws Exception {

        String url = "http://" + clickhouseConfig.getGateway_ip().trim() + "/sys/engine/tasks/"+queryId;
        // 声明httpPost请求
        HttpDelete HttpDelete = new HttpDelete(url);
        // 加入配置信息
        HttpDelete.setConfig(RequestshortConfig);

        // 发起请求
        try(CloseableHttpResponse response = this.httpClient.execute(HttpDelete)) {

            HttpResult rs = new HttpResult();
            rs.setCode(response.getStatusLine().getStatusCode());
            rs.setBody(EntityUtils.toString(response.getEntity(), "UTF-8"));
            return rs;
        }
        catch (Exception e){
        logger.error(e.toString());
            throw new Exception();
    }

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
