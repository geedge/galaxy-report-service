package com.mesa.reportservice.service;


import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Map;

/**
 *
 * @author lijinyang
 * @date 2024/1/24
 */
@Service
public interface ZookeeperService {

     boolean isMaster();

     Map<String,Object> status() throws Exception;
}