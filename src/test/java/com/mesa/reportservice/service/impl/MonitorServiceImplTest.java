package com.mesa.reportservice.service.impl;

import com.mesa.ReportServiceApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Auther: lijinyang
 * @Date: 2024/01/04/16:08
 */
@EnableAutoConfiguration
@AutoConfigureMockMvc
public class MonitorServiceImplTest extends ReportServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetReportStatus() throws Exception{
        mockMvc.perform(get("/monitor"))
                .andExpect(status().isOk());
    }
}
