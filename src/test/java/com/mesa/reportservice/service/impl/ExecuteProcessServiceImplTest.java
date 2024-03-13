package com.mesa.reportservice.service.impl;

import com.mesa.ReportServiceApplicationTests;
import com.mesa.reportservice.bean.JobEntity;
import com.mesa.reportservice.enums.JobStates;
import com.mesa.reportservice.exception.SQSCode;
import com.mesa.reportservice.exception.SQSException;
import com.mesa.reportservice.service.ExecuteProcessService;
import com.mesa.reportservice.util.Constant;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


/**
 * @Auther: lijinyang
 * @Date: 2024/01/08/16:33
 */
@EnableAutoConfiguration
public class ExecuteProcessServiceImplTest extends ReportServiceApplicationTests {

    @Autowired
    private ExecuteProcessService executeProcessService;

    @Test
    public void updateResultMessage() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "okJob", JobEntity.class);
        executeProcessService.updateResultMessage(job);
        Assert.assertEquals(Constant.OK,job.getResultMessage());

        job = this.jsonToInParameter("parameters/executeProcessTest.json", "cancelJob", JobEntity.class);
        executeProcessService.updateResultMessage(job);
        Assert.assertEquals(Constant.CANCEL,job.getResultMessage());

        job = this.jsonToInParameter("parameters/executeProcessTest.json", "dataErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertEquals(Constant.DATABASE_ERROR,job.getResultMessage());
        }

        job = this.jsonToInParameter("parameters/executeProcessTest.json", "paramErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertEquals(Constant.PARAM_SYNTAX_ERROR,job.getResultMessage());
        }

        job = this.jsonToInParameter("parameters/executeProcessTest.json", "SQLErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertEquals(Constant.JOB_ERROR,job.getResultMessage());
        }

        job = this.jsonToInParameter("parameters/executeProcessTest.json", "unknownErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertEquals(Constant.UNKNOWN_ERROR,job.getResultMessage());
        }
    }

    @Test
    public void reSet() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "reSetJob", JobEntity.class);
        executeProcessService.reSet(job);
        Assert.assertEquals(JobStates.PENDING.getValue(),job.getState());
    }

    @Test
    public void killQuery() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "cancelJob", JobEntity.class);
        executeProcessService.killQuery(job);
        Assert.assertTrue(job.getIsValid()==0);
    }

    @Test
    public void updateProcessMessage() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "updateProcessJob", JobEntity.class);
        try{
            executeProcessService.updateProcessMessage(job);
        } catch (SQSException e) {
            Assert.assertEquals(SQSCode.RESPONSE_DATA_ISNULL.getMsg(),e.getMsg());
        }
    }
}
