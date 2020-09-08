package com.mesa.reportservice.configuration;

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
            conf.set("hbase.zookeeper.znode.parent", hbproperties.getZookeeper_znode_parent());
            conf.set("hbase.client.retries.number", hbproperties.getClient_retries_number());
            conf.set("hbase.rpc.timeout", hbproperties.getRpc_timeout());
            conf.set("hbase.client.keyvalue.maxsize", "1024000000");
        }

        return conf;
    }

    @Bean(name = "hbaseConnection")
    public Connection getConnection(@Qualifier("hbaseConfiguration") org.apache.hadoop.conf.Configuration Conf) {
        ExecutorService executor = null;
        Connection con = null;
        try {
            executor = Executors.newFixedThreadPool(hbproperties.getConnect_pool());
            con = ConnectionFactory.createConnection(Conf,executor);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;


    }
}
