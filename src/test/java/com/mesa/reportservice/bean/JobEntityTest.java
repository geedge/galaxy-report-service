package com.mesa.reportservice.bean;

import com.mesa.ReportServiceApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import static org.junit.Assert.*;

/**
 * @Auther: lijinyang
 * @Date: 2024/03/12/17:24
 */
@EnableAutoConfiguration
public class JobEntityTest extends ReportServiceApplicationTests {

    @Test
    public void testClone() {
        JobEntity job = this.jsonToInParameter("parameters/executeTest.json", "executeJob", JobEntity.class);
        Object clone = job.clone();
        Assert.assertTrue(clone instanceof JobEntity);
    }
}