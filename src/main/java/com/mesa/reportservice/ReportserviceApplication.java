package com.mesa.reportservice;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan

@SpringBootApplication
@EnableScheduling
public class ReportserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportserviceApplication.class, args);
    }

/*    @Bean
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient("192.168.40.194:2181,192.168.40.193:2181", new RetryNTimes(5, 1000));
    }*/

}
