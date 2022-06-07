package com.example.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * dashboard 入口
 * <a href="http://localhost:9999/commerce-hystrix-dashboard/hystrix">dashboard 访问路径</a>
 * <a href="http://localhost:8000/commerce-nacos-client/actuator/hystrix.stream">监控地址</a>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard
public class DashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }
}
