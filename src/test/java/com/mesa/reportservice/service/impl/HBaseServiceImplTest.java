package com.mesa.reportservice.service.impl;

import com.mesa.ReportServiceApplicationTests;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.service.HBaseService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


/**
 * @Auther: lijinyang
 * @Date: 2024/01/10/14:48
 */
@EnableAutoConfiguration
public class HBaseServiceImplTest extends ReportServiceApplicationTests {

    @Autowired
    private HBaseService hBaseService;

    @Test
    public void testPut() throws Exception {
        JobEntity job = this.jsonToInParameter("parameters/hBaseTest.json", "putJob", JobEntity.class);
        Boolean put = hBaseService.put(job);
        Assert.assertTrue(put);
    }
}
