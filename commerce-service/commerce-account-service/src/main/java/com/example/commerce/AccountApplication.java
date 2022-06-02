package com.example.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 用户账户微服务
 * <a href="http://localhost:8003/commerce-account-service/doc.html">swagger 文档</a>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
}
