package com.mesa.reportservice.service.impl;

import com.mesa.reportservice.configuration.ZkProperties;
import com.mesa.reportservice.service.ZkService;
import com.mesa.reportservice.util.Logs;
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


    @Override
    public boolean isMaster() {

        try {
            if (zp.getOpen() == 0) {
                boolean isZkCuratorStarted = curatorConnect.isStarted();
                String nodePath = "/masterip";
                System.out.println("当前客户端的状态：" + (isZkCuratorStarted ? "连接中..." : "已关闭..."));
                ExistsBuilder sss = curatorConnect.checkExists();
                Stat statExist = curatorConnect.checkExists().forPath(nodePath);
                InetAddress address = InetAddress.getLocalHost();
                String localip = address.getHostAddress().trim();
                if (statExist == null) {
                    System.out.println(address.getHostAddress());
                    byte[] data = address.getHostAddress().getBytes();  // 节点数据

                    String result = curatorConnect.create().creatingParentsIfNeeded()  // 创建父节点，也就是会递归创建
                            .withMode(CreateMode.EPHEMERAL)  // 节点类型
                            .forPath(nodePath, data);
                    System.out.println(result + "节点，创建成功...");
                    return true;

                } else {
                    System.out.println(nodePath + " 节点存在");
                    Stat stat = new Stat();

                    byte[] nodeData = curatorConnect.getData().storingStatIn(stat).forPath(nodePath);
                    String masterip = new String(nodeData).trim();
                    System.out.println("节点 " + nodePath + " 的数据为：" + new String(nodeData));

                    if (masterip.equals(localip)) {

                        return true;

                    } else {
                        return false;

                    }
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            Logs.error(e.toString());
            return true;
        }
    }

}
