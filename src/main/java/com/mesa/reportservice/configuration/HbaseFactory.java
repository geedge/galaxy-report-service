package com.mesa.reportservice.configuration;

import cn.hutool.log.Log;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2020/3/10.
 */
@Configuration
@EnableConfigurationProperties(HbaseProperties.class)
public class HbaseFactory {
    private final HbaseProperties hbproperties;

    Log logger = Log.get();

    @Autowired
    public HbaseFactory(HbaseProperties hbproperties) {
        this.hbproperties = hbproperties;
    }

    @Bean(name = "hbaseConfiguration")
    public org.apache.hadoop.conf.Configuration getConfiguration() {

        org.apache.hadoop.conf.Configuration conf = null;
        if (conf == null) {
            conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", hbproperties.getZookeeper_quorum());
            conf.set("hbase.zookeeper.property.clientPort", hbproperties.getZookeeper_property_clientPort());
            conf.set("zookeeper.znode.parent", hbproperties.getZookeeper_znode_parent());
            conf.set("hbase.client.retries.number", hbproperties.getClient_retries_number());
            conf.set("hbase.rpc.timeout", hbproperties.getRpc_timeout());
            conf.set("hbase.client.keyvalue.maxsize", "1024000000");
            conf.set("zookeeper.recovery.retry", "3");
            conf.set("hbase.client.ipc.pool.size", hbproperties.getConnect_pool().toString());


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
