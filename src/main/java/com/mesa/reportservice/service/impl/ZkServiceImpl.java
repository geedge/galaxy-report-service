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
                System.out.println("当前客户端的状态：" + (isZkCuratorStarted ? "连接中..." : "已关闭..."));
                ExistsBuilder eb = curatorConnect.checkExists();
                Stat statExist = curatorConnect.checkExists().forPath(nodePath);
                if (statExist == null) {
                    byte[] data = GlobelConfig.zkuuid.getBytes();  // 节点数据

                    String result = curatorConnect.create().creatingParentsIfNeeded()  // 创建父节点，也就是会递归创建
                            .withMode(CreateMode.EPHEMERAL)  // 节点类型
                            .forPath(nodePath, data);
                    return true;

                } else {
                    System.out.println(nodePath + " 节点存在");
                    Stat stat = new Stat();

                    byte[] nodeData = curatorConnect.getData().storingStatIn(stat).forPath(nodePath);
                    String masterid = new String(nodeData).trim();
                    System.out.println("uuid="+ GlobelConfig.zkuuid+" 节点 " + nodePath + " 的数据为：" + new String(nodeData));

                    if (masterid.equals(GlobelConfig.zkuuid)) {

                        return true;

                    } else {
                        logger.debug("不是主节点");
                        return false;
                    }
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return true;
        }
    }

}
