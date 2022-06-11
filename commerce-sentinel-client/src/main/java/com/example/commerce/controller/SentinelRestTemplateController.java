package com.example.commerce.controller;

import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 使用 sentinel 保护 RestTemplate 的服务调用
 */
@Slf4j
@RestController
@RequestMapping("sentinel-rest-template")
public class SentinelRestTemplateController {

    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    public SentinelRestTemplateController(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    /**
     * 使用 restTemplate 获取 token；
     * 流控降级：针对簇点链路中的 url
     * 容错降级：服务不可用时不会生效 (restTemplate 出错后只能处理 BlockException)
     */
    @PostMapping("get-token")
    public JwtToken getToken(@RequestBody UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        String url = "http://localhost:7000/commerce-authority-center/authority/token";
        log.info("获取 token，url: {}, usernameAndPassword: {}", url, usernameAndPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.postForObject(url,
                new HttpEntity<>(mapper.writeValueAsString(usernameAndPassword), headers),
                JwtToken.class);
    }
}
