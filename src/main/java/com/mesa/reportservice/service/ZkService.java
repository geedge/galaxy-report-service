package com.mesa.reportservice.service;


import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * Created by wk1 on 2020/1/6.
 */
@Service
public interface ZkService {


     boolean isMaster() throws Exception;
}