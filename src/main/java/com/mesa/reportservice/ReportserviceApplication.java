package com.mesa.reportservice;

import com.mesa.reportservice.util.StringUtil;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.TimeZone;

@ServletComponentScan

@SpringBootApplication
@EnableScheduling
public class ReportserviceApplication {
    public static final String TIMEZONE = "UTC";

    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.password", "galaxy");
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = runtimeMxBean.getInputArguments();
        if (inputArguments.stream().noneMatch(o -> o.contains("user.timezone"))
                && StringUtil.isEmpty(System.getenv().get("TZ"))) {
            // java.util.Date
            TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE));
            // joda.time.DateTime
            System.setProperty("user.timezone", TIMEZONE);
        }
        SpringApplication.run(ReportserviceApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName){
        return registry -> registry.config().commonTags("application", applicationName);
    }
/*    @Bean
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient("192.168.40.194:2181,192.168.40.193:2181", new RetryNTimes(5, 1000));
    }*/

}
