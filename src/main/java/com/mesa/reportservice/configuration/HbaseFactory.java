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
            // conf.set("hase.client.pause", client_pause);
            conf.set("hbase.client.retries.number", hbproperties.getClient_retries_number());
            conf.set("hbase.rpc.timeout", hbproperties.getRpc_timeout());
            // conf.set("hbase.client.operation.timeout", client_operation_timeout);
            // conf.set("hbase.client.scanner.timeout.period", client_scanner_timeout_period);
            conf.set("hbase.client.keyvalue.maxsize", "1024000000");
            //conf.set("hbase.client.write.buffer", "20971520");


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
    /*@Bean(name = "hbaseTable")
    public Table getTable(@Qualifier("hbaseConnection") Connection con)  {

        Table table=null;
        try {

            ExecutorService pool = Executors.newFixedThreadPool(10);
            BufferedMutatorParams bmp = new BufferedMutatorParams();
            ss.writeBufferSize();
            ss.setWriteBufferPeriodicFlushTimerTickMs()
            BufferedMutatorParams aaa = con.getBufferedMutator()
            aaa.writeBufferSize(1000000);

            BufferedMutator aaa=null;
            aaa
            table = con.getTable(TableName.valueOf("tsg:t1"),pool);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;


    }*/

  /*  @Bean(name = "hbaseExceptionListener")
    public BufferedMutator.ExceptionListener getLishtener(@Qualifier("hbaseConnection") Connection con) {

        BufferedMutator.ExceptionListener listener = new BufferedMutator.ExceptionListener() {

            public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) {
                for (int i = 0; i < e.getNumExceptions(); i++) {
                    System.out.print("Failed to sent put " + e.getRow(i) + ".");
                }
            }
        };
        return listener;


    }*/

}
