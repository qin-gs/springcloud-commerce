package com.example.commerce.service.communication;

import com.example.commerce.constant.CommonConstant;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 微服务通信 (从注册中心获取 token)
 */
@Slf4j
@Service
public class UseRestTemplateService {

    private final ObjectMapper mapper;
    /**
     * 负载均衡客户端
     */
    private final LoadBalancerClient loadBalancerClient;

    public UseRestTemplateService(ObjectMapper mapper, LoadBalancerClient loadBalancerClient) {
        this.mapper = mapper;
        this.loadBalancerClient = loadBalancerClient;
    }

    /**
     * 写死 host, port, path
     */
    public JwtToken getJwtTokenByRestTemplate(UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        String url = "http://localhost:7000/commerce-authority-center/authority/token";
        log.info("请求地址：{}, 请求信息：{}", url, usernameAndPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RestTemplate().postForObject(
                url,
                new HttpEntity<>(mapper.writeValueAsString(usernameAndPassword), headers),
                JwtToken.class
        );
    }

    /**
     * 从注册中心获取请求地址，实现负载均衡
     */
    public JwtToken getJwtTokenLoadBalance(UsernameAndPassword usernameAndPassword) throws JsonProcessingException {

        ServiceInstance serviceInstance = loadBalancerClient.choose(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
        log.info("nacos {}, {}, {}", serviceInstance.getServiceId(), serviceInstance.getInstanceId(), serviceInstance.getMetadata());

        String url = String.format("http://%s:%s/commerce-authority-center/authority/token", serviceInstance.getHost(), serviceInstance.getPort());
        log.info("请求地址：{}, 请求信息：{}", url, usernameAndPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RestTemplate().postForObject(
                url,
                new HttpEntity<>(mapper.writeValueAsString(usernameAndPassword), headers),
                JwtToken.class
        );
    }
}
