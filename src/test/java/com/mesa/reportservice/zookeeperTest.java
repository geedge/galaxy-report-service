package com.mesa.reportservice;

import com.mesa.ReportServiceApplicationTests;
import com.mesa.reportservice.service.ZookeeperService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * @Auther: lijinyang
 * @Date: 2024/01/10/14:20
 */
@EnableAutoConfiguration
public class zookeeperTest extends ReportServiceApplicationTests {

    @Autowired
    private ZookeeperService zookeeperService;

    @Test
    public void testIsMaster() throws Exception {
        boolean isMaster = zookeeperService.isMaster();
        Assert.assertTrue(isMaster);
    }
}
