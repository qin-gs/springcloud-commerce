package com.example.commerce.controller;

import com.example.commerce.service.NacosClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("nacos-client")
public class NacosClientController {

    private NacosClientService service;

    public NacosClientController(NacosClientService service) {
        this.service = service;
    }

    /**
     * 根据 service id 获取服务信息
     */
    @GetMapping("/service-instance")
    public List<ServiceInstance> getNacosClientInfo(@RequestParam(defaultValue = "commerce-nacos-client") String serviceId) {
        log.info("service id: {}", serviceId);
        return service.getNacosInfo(serviceId);
    }

}
