package com.mesa.reportservice.configuration;

import cn.hutool.log.Log;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wk1 on 2020/1/6.
 */
@Configuration
public class ZkConfig {

    @Autowired
    private  ZkProperties zkproperties;
    Log logger = Log.get();



    @Bean(name = "curatorConnect")
    public CuratorFramework CuratorConnect() {

        CuratorFramework client = null;


        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkproperties.getElapsedTimeMs(), zkproperties.getRetryCount());

        // 实例化Curator客户端
        client = CuratorFrameworkFactory.builder() // 使用工厂类来建造客户端的实例对象
                .connectString(zkproperties.getConnectString())  // 放入zookeeper服务器ip
                .sessionTimeoutMs(zkproperties.getSessionTimeoutMs()).connectionTimeoutMs(zkproperties.getConnectionTimeoutMs()).retryPolicy(retryPolicy)  // 设定会话时间以及重连策略
                .namespace(zkproperties.getNameSpace()).build();  // 设置命名空间以及开始建立连接
        if (zkproperties.getOpen() == 0) {
            client.start();
        } else {
            logger.info("repoertservice start local");
        }

        return client;
    }


}
