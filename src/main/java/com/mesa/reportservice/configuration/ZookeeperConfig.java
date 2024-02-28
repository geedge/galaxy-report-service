package com.mesa.reportservice.configuration;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Configuration
public class ZookeeperConfig {

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    private static final Log logger = LogFactory.get();

    @Bean(name = "curatorConnect")
    public CuratorFramework curatorConnect() {
        CuratorFramework client = null;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getElapsedTimeMs(), zookeeperProperties.getRetryCount());
        // 实例化Curator客户端
        // 使用工厂类来建造客户端的实例对象
        client = CuratorFrameworkFactory.builder()
                // 放入zookeeper服务器ip
                .connectString(zookeeperProperties.getConnectString())
                // 设定会话时间以及重连策略
                .sessionTimeoutMs(zookeeperProperties.getSessionTimeoutMs()).connectionTimeoutMs(zookeeperProperties.getConnectionTimeoutMs()).retryPolicy(retryPolicy)
                // 设置命名空间以及开始建立连接
                .namespace(zookeeperProperties.getNameSpace()).build();
        if (zookeeperProperties.getOpen() == 0) {
            client.start();
        } else {
            logger.info("reportService start local");
        }
        return client;
    }
}
