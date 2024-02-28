package com.mesa.reportservice.configuration;

import cn.hutool.log.Log;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Configuration
public class HBaseFactory {

    @Autowired
    private HBaseProperties hBaseProperties;

    Log logger = Log.get();

    @Bean(name = "hbaseConfiguration")
    public org.apache.hadoop.conf.Configuration getConfiguration() {
        org.apache.hadoop.conf.Configuration conf = null;
        if (conf == null) {
            conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", hBaseProperties.getZookeeperQuorum());
            conf.set("hbase.zookeeper.property.clientPort", hBaseProperties.getZookeeperPropertyClientPort());
            conf.set("zookeeper.znode.parent", hBaseProperties.getZookeeperZnodeParent());
            conf.set("hbase.client.retries.number", hBaseProperties.getClientRetriesNumber());
            conf.set("hbase.rpc.timeout", hBaseProperties.getRpcTimeout());
            conf.set("hbase.client.keyvalue.maxsize", "1024000000");
            conf.set("zookeeper.recovery.retry", "3");
            conf.set("hbase.client.ipc.pool.size", hBaseProperties.getConnectPool().toString());
        }
        return conf;
    }

    @Bean(name = "hbaseConnection")
    public Connection getConnection(@Qualifier("hbaseConfiguration") org.apache.hadoop.conf.Configuration Conf) {
        Connection con = null;
        try {
            con = ConnectionFactory.createConnection(Conf);
        } catch (IOException e) {
            logger.error(e.toString());
        }
        return con;
    }
}
