package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import com.mesa.reportservice.configuration.GlobelConfig;
import com.mesa.reportservice.configuration.ZkProperties;
import com.mesa.reportservice.service.ZkService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * Created by wk1 on 2020/1/6.
 */
@Service
public class ZkServiceImpl implements ZkService {
    @Autowired
    private CuratorFramework curatorConnect;
    @Autowired
    private ZkProperties zp;
    Log logger = Log.get();


    @Override
    public boolean isMaster() {


        try {
            if (zp.getOpen() == 0) {
                boolean isZkCuratorStarted = curatorConnect.isStarted();
                String nodePath = "/masterip";
                logger.debug("the current state of the client: " + (isZkCuratorStarted ? "connecting..." : "closed..."));
                Stat statExist = curatorConnect.checkExists().forPath(nodePath);
                if (statExist == null) {
                    byte[] data = GlobelConfig.zkuuid.getBytes();  // 节点数据

                    String result = curatorConnect.create().creatingParentsIfNeeded()  // 创建父节点，也就是会递归创建
                            .withMode(CreateMode.EPHEMERAL)  // 节点类型
                            .forPath(nodePath, data);
                    logger.debug(result);

                    return true;

                } else {
                    logger.debug(nodePath + " node exists");
                    Stat stat = new Stat();

                    byte[] nodeData = curatorConnect.getData().storingStatIn(stat).forPath(nodePath);
                    String masterid = new String(nodeData, "UTF-8").trim();
                    logger.debug("uuid=" + GlobelConfig.zkuuid + " node " + nodePath + " data is:" + masterid);

                    if (masterid.equals(GlobelConfig.zkuuid)) {

                        return true;

                    } else {
                        logger.debug("not the main node");
                        return false;
                    }
                }
            } else {
                return true;
            }

        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

}


