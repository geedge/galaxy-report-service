package com.mesa.reportservice;

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
public class ExecuteProcessTest extends ReportServiceApplicationTests {

    @Autowired
    private ExecuteProcessService executeProcessService;

    @Test
    public void testReSet() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "reSetJob", JobEntity.class);
        executeProcessService.reSet(job);
        Assert.assertEquals(JobStates.PENDING.getValue(),job.getState());
    }

    @Test
    public void testUpdateResultMessageCancel() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "cancelJob", JobEntity.class);
        executeProcessService.updateResultMessage(job);
        Assert.assertEquals(Constant.CANCEL,job.getResultMessage());
    }

    @Test
    public void testUpdateResultMessageOk() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "okJob", JobEntity.class);
        executeProcessService.updateResultMessage(job);
        Assert.assertEquals(Constant.OK,job.getResultMessage());
    }

    @Test
    public void testUpdateResultMessageDataError() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "dataErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertNotEquals((Object) SQSCode.SUCCESS.getCode(),e.getCode());
        }
    }

    @Test
    public void testUpdateResultMessageParamError() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "paramErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertNotEquals((Object) SQSCode.SUCCESS.getCode(),e.getCode());
        }
    }

    @Test
    public void testUpdateResultMessageSQLError() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "SQLErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertNotEquals((Object) SQSCode.SUCCESS.getCode(),e.getCode());
        }
    }

    @Test
    public void testUpdateResultMessageUnknownError() {
        JobEntity job = this.jsonToInParameter("parameters/executeProcessTest.json", "unknownErrorJob", JobEntity.class);
        try {
            executeProcessService.updateResultMessage(job);
        } catch (SQSException e) {
            Assert.assertNotEquals((Object) SQSCode.SUCCESS.getCode(),e.getCode());
        }
    }
}
