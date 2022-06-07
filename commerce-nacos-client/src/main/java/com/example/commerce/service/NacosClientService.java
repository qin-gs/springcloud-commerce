package com.example.commerce.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Slf4j
@Service
public class NacosClientService {

    /**
     * 服务发现的一个客户端 (从注册中心获取服务信息)，SpringCloud 提供的接口，alibaba 进行实现
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

    /**
     * 提供给请求合并
     */
    public List<List<ServiceInstance>> getNacosClientInfos(List<String> serviceIds) {
        log.info("批量获取服务信息: {}", serviceIds);
        ArrayList<List<ServiceInstance>> result = new ArrayList<>(serviceIds.size());
        for (String serviceId : serviceIds) {
            result.add(client.getInstances(serviceId));
        }
        return result;
    }

    /**
     * 使用注解完成 hystrix 请求合并
     * 批量方法，所有请求都合并，请求窗口 200ms内的请求都会被合并
     */
    @HystrixCollapser(batchMethod = "findNacosClientInfos",
            scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL,
            collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds", value = "200")}
    )
    public Future<List<ServiceInstance>> findNacosClientInfo(String serviceId) {
        // 系统正常运行，不会走这个方法
        throw new RuntimeException("系统正常运行，不会走这个方法");
    }

    @HystrixCommand
    public List<List<ServiceInstance>> findNacosClientInfos(List<String> serviceIds) {
        log.info("批量获取服务信息: {}", serviceIds);
        return getNacosClientInfos(serviceIds);
    }

}
