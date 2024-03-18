package com.mesa.reportservice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.mesa.ReportServiceApplicationTests;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.enums.JobStates;
import com.mesa.reportservice.exception.SQSCode;
import com.mesa.reportservice.exception.SQSException;
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
public class JobServiceImplTest extends ReportServiceApplicationTests {

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
        JobEntity job = this.jsonToInParameter("parameters/jobTest.json", "updateProcessesSuccessJob", JobEntity.class);
        int i = jobService.updateProcesses(job);
        Assert.assertEquals(updateProcessesSuccess,i);
    }

    @Test
    public void testUpdateProcessesError() {
        JobEntity job = this.jsonToInParameter("parameters/jobTest.json", "updateProcessesErrorJob", JobEntity.class);
        int i = jobService.updateProcesses(job);
        Assert.assertNotEquals(updateProcessesSuccess,i);
    }

    @Test
    public void testGetJobState() {
        String successId = this.jsonToInParameter("parameters/jobTest.json", "getJobDetailSuccessId", String.class);
        Map<String, Object> success = jobService.getJobDetail(successId);
        Assert.assertTrue(ObjectUtil.isNotEmpty(success));

        String errorId = this.jsonToInParameter("parameters/jobTest.json", "getJobDetailErrorId", String.class);
        try {
            jobService.getJobDetail(errorId);
        } catch (SQSException e) {
            Assert.assertTrue(e.getCode()==SQSCode.JOB_IS_NOT_EXIST.getCode());
        }
    }
}
