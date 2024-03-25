package com.mesa.reportservice.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mesa.reportservice.configuration.SchedulerProperties;
import com.mesa.reportservice.configuration.ZookeeperProperties;
import com.mesa.reportservice.service.ZookeeperService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public class ZookeeperServiceImpl implements ZookeeperService {

    @Autowired
    private CuratorFramework curatorConnect;

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    private static final Log logger = LogFactory.get();

    @Override
    public boolean isMaster() {
        try {
            if (zookeeperProperties.getOpen() == 0) {
                boolean isZkCuratorStarted = curatorConnect.isStarted();
                String nodePath = "/masterip";
                logger.debug("the current state of the client: " + (isZkCuratorStarted ? "connecting..." : "closed..."));
                Stat statExist = curatorConnect.checkExists().forPath(nodePath);
                if (statExist == null) {
                    // 节点数据
                    byte[] data = SchedulerProperties.zkUUID.getBytes(StandardCharsets.UTF_8);
                    // 创建父节点，也就是会递归创建
                    String result = curatorConnect.create().creatingParentsIfNeeded()
                            // 节点类型
                            .withMode(CreateMode.EPHEMERAL)
                            .forPath(nodePath, data);
                    logger.debug(result);
                    return true;
                } else {
                    logger.debug(nodePath + " node exists");
                    Stat stat = new Stat();
                    byte[] nodeData = curatorConnect.getData().storingStatIn(stat).forPath(nodePath);
                    String masterId = new String(nodeData, StandardCharsets.UTF_8).trim();
                    logger.debug("uuid=" + SchedulerProperties.zkUUID + " node " + nodePath + " data is:" + masterId);
                    if (masterId.equals(SchedulerProperties.zkUUID)) {
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

    @Override
    public Map<String,Object> status() throws Exception {
        Map<String, Object> status = new HashMap<>();
        status.put("status","UP");
        if (zookeeperProperties.getOpen() == 0) {
            List<String> children = curatorConnect.getChildren().forPath("/");
            boolean isDown = true;
            for (String child : children) {
                Stat stat = curatorConnect.checkExists().forPath("/" + child);
                if (stat != null) {
                    isDown = false;
                }
            }
            if(isDown){
                status.put("status","DOWN");
            }
        }
        return status;
    }
}


