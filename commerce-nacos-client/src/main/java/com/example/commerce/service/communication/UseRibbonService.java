package com.example.commerce.service.communication;

import com.example.commerce.constant.CommonConstant;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UseRibbonService {
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public UseRibbonService(ObjectMapper mapper, RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    /**
     * 通过 ribbon 进行微服务通信
     */
    public JwtToken getJwtTokenByRibbon(UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        // 通过 ribbon 可以用 服务名 替换 host, port
        String url = String.format("http://%s/commerce-authority-center/authority/token", CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
        log.info("请求地址：{}, 请求信息：{}", url, usernameAndPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForObject(
                url,
                new HttpEntity<>(mapper.writeValueAsString(usernameAndPassword), headers),
                JwtToken.class
        );
    }

    /**
     * 通过原生代码使用 ribbon 进行微服务通信
     */
    public JwtToken getJwtTokenByRibbonRaw(UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        // 通过 ribbon 可以用 服务名 替换 host, port
        String url = "http://%s/commerce-authority-center/authority/token";

        // 获取服务提供方所有的地址 和 端口号
        List<ServiceInstance> instances = discoveryClient.getInstances(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
        // 构造 ribbon 服务列表
        ArrayList<Server> servers = new ArrayList<>();
        instances.forEach(instance -> servers.add(new Server(instance.getHost(), instance.getPort())));

        // 通过负载均衡完成服务调用
        BaseLoadBalancer loadBalancer = LoadBalancerBuilder.newBuilder()
                .buildFixedServerListLoadBalancer(servers);
        // 设置负载均衡策略
        loadBalancer.setRule(new RetryRule(new RandomRule(), 3));

        String result = LoadBalancerCommand.builder()
                .withLoadBalancer(loadBalancer)
                .build().submit((server) -> {
                    try {
                        String target = String.format(url, String.format("%s:%s", server.getHost(), server.getPort()));
                        log.info("请求地址：{}, 请求信息：{}", target, usernameAndPassword);
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        String token = new RestTemplate().postForObject(
                                target,
                                new HttpEntity<>(mapper.writeValueAsString(usernameAndPassword), headers),
                                String.class
                        );
                        return Observable.just(token);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toBlocking().first().toString();
        return mapper.readValue(result, JwtToken.class);
    }


}
