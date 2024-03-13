package com.mesa.reportservice.service.impl;

import com.mesa.ReportServiceApplicationTests;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.enums.JobStates;
import com.mesa.reportservice.service.ExecuteService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


/**
 * @Auther: lijinyang
 * @Date: 2024/01/10/15:57
 */
@EnableAutoConfiguration
public class ExecuteServiceImplTest extends ReportServiceApplicationTests {

    @Autowired
    private ExecuteService executeService;

    @Test
    public void testExecuteTask() {
        JobEntity job = this.jsonToInParameter("parameters/executeTest.json", "executeJob", JobEntity.class);
        executeService.queryQgw(job);
        Assert.assertEquals(JobStates.DONE.getValue(),job.getState());
    }
}
