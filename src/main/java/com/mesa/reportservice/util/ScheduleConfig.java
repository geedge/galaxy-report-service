package com.mesa.reportservice.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * Created by wk1 on 2019/6/21.
 */

//@Configuration
//所有的定时任务都放在一个线程池中，定时任务启动时使用不同都线程。
public class ScheduleConfig  {
/*    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        //设定一个长度10的定时任务线程池
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
    }*/
}
