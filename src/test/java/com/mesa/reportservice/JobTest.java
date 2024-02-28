package com.mesa.reportservice;

import com.mesa.ReportServiceApplicationTests;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.enums.JobStates;
import com.mesa.reportservice.service.JobService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lijinyang
 * @Date: 2024/01/08/17:22
 */
@EnableAutoConfiguration
public class JobTest extends ReportServiceApplicationTests {

    private static final int rows = 10;

    private static final int updateProcessesSuccess = 1;

    @Autowired
    private JobService jobService;

    @Test
    public void testGetRunningJob() {
        List<JobEntity> jobForExecute = jobService.getRunningJob();
        Assert.assertNotNull(jobForExecute);
        Assert.assertEquals(JobStates.RUNNING.getValue(),jobForExecute.get(0).getState());
    }

    @Test
    public void testGetJobTask() {
        List<JobEntity> jobForExecute = jobService.getJobTask(rows);
        Assert.assertNotNull(jobForExecute);
        Assert.assertEquals(JobStates.PENDING.getValue(),jobForExecute.get(0).getState());
    }

    @Test
    public void testGetJobCount() {
        Map<String, Long> jobCount = jobService.getJobCount();
        Assert.assertNotNull(jobCount);
    }

    @Test
    public void testUpdateProcessesSuccess() {
        JobEntity job = this.jsonToInParameter("parameters/mysqlTest.json", "updateProcessesSuccessJob", JobEntity.class);
        int i = jobService.updateProcesses(job);
        Assert.assertEquals(updateProcessesSuccess,i);
    }

    @Test
    public void testUpdateProcessesError() {
        JobEntity job = this.jsonToInParameter("parameters/mysqlTest.json", "updateProcessesErrorJob", JobEntity.class);
        int i = jobService.updateProcesses(job);
        Assert.assertNotEquals(updateProcessesSuccess,i);
    }
}
