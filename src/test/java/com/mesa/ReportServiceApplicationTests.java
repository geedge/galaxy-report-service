package com.mesa;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.mesa.reportservice.ReportServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReportServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ActiveProfiles("test")
public class ReportServiceApplicationTests {

  private static final Log logger = LogFactory.get();

  @Autowired
  private DataSource dataSource;

  @LocalServerPort
  protected int testPort;

  static {
    System.setProperty("jasypt.encryptor.password", "galaxy");
    System.setProperty("JM.SNAPSHOT.PATH", "config");
  }

  public ReportServiceApplicationTests() {
  }

  @Test
  public void contextLoads() {
    logger.info("SAVED QUERY SCHEDULER CONTEXT LOAD");
  }

  @Test
  public void runSqlScript() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/SavedQueryJob.sql"));
    }
  }

  // 解析resource: parameters/otherPath/指定JSON文件, 返回指定类型对象
  public <T> T jsonToInParameter(String path, String parameterName,Class<T> type) {
    InputStream resourceAsStream = ReportServiceApplicationTests.class.getClassLoader().getResourceAsStream(path);
    if (Objects.isNull(resourceAsStream)) {
      logger.info("Unable to obtain the specified file under the specified path. Please check if the file exists or if the file name capitalization is consistent", path);
      throw new IllegalArgumentException();
    }
    Map<String, Object> json = null;
    try {
      json = JSON.parseObject(resourceAsStream, Map.class);
    } catch (IOException e) {
      logger.error("Failed to read files from the test resource directory, file: {}", path);
      throw new RuntimeException(e);
    }
    Object data = json.get(parameterName);
    String objectJsonStr = JSON.toJSONString(data, true);
    logger.info("parameter file: {}, parameter name: {}", path, parameterName);
    return JSON.parseObject(objectJsonStr, type);
  }

}
