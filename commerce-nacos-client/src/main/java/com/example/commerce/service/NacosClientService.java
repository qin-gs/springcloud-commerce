package com.example.commerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NacosClientService {

    /**
     * 服务发现的一个客户端 (从注册中心获取服务信息)，springcloud 提供的接口，alibaba 进行实现
     */
    private final DiscoveryClient client;

    public NacosClientService(DiscoveryClient client) {
        this.client = client;
    }

    /**
     * 服务发现：获取某个 serviceId 下所有的服务信息
     */
    public List<ServiceInstance> getNacosInfo(String serviceId) {
        log.info("获取服务信息：{}", serviceId);
        return client.getInstances(serviceId);
    }
}
