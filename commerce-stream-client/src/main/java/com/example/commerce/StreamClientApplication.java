package com.example.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 基于 SpringCloud Stream 构建消息驱动微服务；
 * nacos, kafka, sleuth, zipkin
 */
@SpringBootApplication
@EnableDiscoveryClient
public class StreamClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamClientApplication.class, args);
    }
}
