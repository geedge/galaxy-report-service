package com.mesa.reportservice.service;


import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public interface ZookeeperService {

     boolean isMaster();
}