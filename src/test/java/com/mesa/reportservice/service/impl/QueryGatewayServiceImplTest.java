package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mesa.ReportServiceApplicationTests;
import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.exception.SQSException;
import com.mesa.reportservice.service.QueryGatewayService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mesa.reportservice.exception.SQSCode.ERROR;

/**
 * @Auther: lijinyang
 * @Date: 2024/01/04/17:05
 */
@EnableAutoConfiguration
public class QueryGatewayServiceImplTest extends ReportServiceApplicationTests {

    private static final String QUERY_ID_PATTERN = "^[a-f0-9]{32}:[a-f0-9]{32}$";

    private static final Log logger = LogFactory.get();

    @Autowired
    private QueryGatewayService queryGatewayService;

    @Test
    public void testGetQueryIdSuccess() {
        String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "querySqlSuccess", String.class);
        String queryId = queryGatewayService.getQueryId(sql);
        Pattern compile = Pattern.compile(QUERY_ID_PATTERN);
        Matcher matcher = compile.matcher(queryId);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void testGetQueryIdError() {
        String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "querySqlError", String.class);
        String queryId = queryGatewayService.getQueryId(sql);
        Pattern compile = Pattern.compile(QUERY_ID_PATTERN);
        Matcher matcher = compile.matcher(queryId);
        Assert.assertTrue(!matcher.find());
    }

    @Test
    public void testQueryJobSuccess() throws IOException {
        String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "querySqlSuccess", String.class);
        HttpResult httpResult = null;
        httpResult = queryGatewayService.queryJob(sql);
        Assert.assertTrue(httpResult.getCode() == HttpStatus.OK.value());
    }

    @Test
    public void testQueryJobError() throws IOException {
        String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "querySqlError", String.class);
        HttpResult httpResult = null;
        httpResult = queryGatewayService.queryJob(sql);
        Assert.assertTrue(httpResult.getCode() == HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testQueryJobProcessSuccess() throws IOException {
        String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "querySqlSuccess", String.class);
        String queryId = queryGatewayService.getQueryId(sql);
        HttpResult httpResult = null;
        httpResult = queryGatewayService.queryJobProcess(queryId);
        Assert.assertTrue(httpResult.getCode() == HttpStatus.OK.value());
        Assert.assertNotNull(httpResult.getBody());
    }

    @Test
    public void testQueryJobProcessError() throws IOException {
        String queryId = this.jsonToInParameter("parameters/queryGatewayTest.json", "queryForProcessIdError", String.class);
        HttpResult httpResult = null;
        httpResult = queryGatewayService.queryJobProcess(queryId);
        Assert.assertTrue(httpResult.getCode() == HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testDeleteJobSuccess() throws IOException {
        String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "querySqlSuccess", String.class);
        String queryId = queryGatewayService.getQueryId(sql);
        HttpResult httpResult = null;
        httpResult = queryGatewayService.deleteJob(queryId);
        Assert.assertTrue(httpResult.getCode() == HttpStatus.OK.value());
    }

    @Test
    public void testDeleteJobError() throws IOException {
        String queryId = this.jsonToInParameter("parameters/queryGatewayTest.json", "queryForCancelIdError", String.class);
        HttpResult httpResult = null;
        httpResult = queryGatewayService.deleteJob(queryId);
        Assert.assertTrue(httpResult.getCode() == HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void highLatencyQuerySuccess() throws IOException {
        String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "highLatencyQuerySuccess", String.class);
        HttpResult httpResult = queryGatewayService.queryJob(sql);
        Assert.assertTrue(httpResult.getCode() == HttpStatus.OK.value());
    }

    @Test
    public void highLatencyQueryCancel() throws IOException {
        try {
            String sql = this.jsonToInParameter("parameters/queryGatewayTest.json", "highLatencyQueryCancel", String.class);
            String queryId = queryGatewayService.getQueryId(sql);
            Thread thread = new Thread(() -> {
                try {
                    HttpResult result = queryGatewayService.queryJob(sql);
                    logger.info(result.getBody());
                    if(!result.getBody().contains("Error in SQL execution: Code: 394. DB::Exception: Query was cancelled. (QUERY_WAS_CANCELLED)")) {
                        throw new SQSException("Job not cancelled", ERROR.getCode());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            thread.sleep(5000);
            HttpResult process = queryGatewayService.queryJobProcess(queryId);
            logger.info(process.getBody());
            Assert.assertTrue(process.getBody().contains("elapsed"));
            HttpResult httpResult = queryGatewayService.deleteJob(queryId);
            Assert.assertTrue(httpResult.getCode() == HttpStatus.OK.value());
            thread.checkAccess();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
