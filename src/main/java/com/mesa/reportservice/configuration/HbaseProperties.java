package com.mesa.reportservice.configuration;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2020/3/10.
 */

//@ConfigurationProperties(prefix = "hbase")
@Component
@NacosConfigurationProperties(prefix = "hbase", dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", type = ConfigType.YAML, autoRefreshed = true)

public class HbaseProperties {

    private String zookeeper_quorum;

    private String zookeeper_property_clientPort;

    private String zookeeper_znode_parent;

    private String client_pause;

    private String client_retries_number;

    private String rpc_timeout;

    private Integer connect_pool;

    private String table;

    private Integer cell_ttl_d;



    public String getZookeeper_quorum() {
        return zookeeper_quorum;
    }

    public void setZookeeper_quorum(String zookeeper_quorum) {
        this.zookeeper_quorum = zookeeper_quorum;
    }

    public String getZookeeper_property_clientPort() {
        return zookeeper_property_clientPort;
    }

    public void setZookeeper_property_clientPort(String zookeeper_property_clientPort) {
        this.zookeeper_property_clientPort = zookeeper_property_clientPort;
    }

    public String getZookeeper_znode_parent() {
        return zookeeper_znode_parent;
    }

    public void setZookeeper_znode_parent(String zookeeper_znode_parent) {
        this.zookeeper_znode_parent = zookeeper_znode_parent;
    }

    public String getClient_pause() {
        return client_pause;
    }

    public void setClient_pause(String client_pause) {
        this.client_pause = client_pause;
    }

    public String getClient_retries_number() {
        return client_retries_number;
    }

    public void setClient_retries_number(String client_retries_number) {
        this.client_retries_number = client_retries_number;
    }

    public String getRpc_timeout() {
        return rpc_timeout;
    }

    public void setRpc_timeout(String rpc_timeout) {
        this.rpc_timeout = rpc_timeout;
    }


    public Integer getConnect_pool() {
        return connect_pool;
    }

    public void setConnect_pool(Integer connect_pool) {
        this.connect_pool = connect_pool;
    }


    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setCell_ttl_d(Integer cell_ttl_d) {
        this.cell_ttl_d = cell_ttl_d;
    }

    public Integer getCell_ttl_d() {
        return cell_ttl_d;
    }
}
