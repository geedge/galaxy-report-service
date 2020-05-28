package com.mesa.reportservice;


import com.mesa.reportservice.bean.HttpResult;
import com.mesa.reportservice.util.ziputil;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportserviceApplicationTests {




	@Autowired
	private CloseableHttpClient httpClient;

	@Autowired
	private RequestConfig config;




	@Test
	public void contextLoads() throws IOException {



	}


}
