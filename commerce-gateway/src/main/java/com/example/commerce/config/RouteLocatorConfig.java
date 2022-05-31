package com.example.commerce.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置登录请求转发规则
 */
@Configuration
public class RouteLocatorConfig {

    /**
     * 用代码定义路由规则，网关层面拦截请求
     */
    @Bean
    public RouteLocator loginRouteLocator(RouteLocatorBuilder builder) {
        // 手动定义网关的路由规则
        return builder.routes()
                .route("commerce-authority",
                        r -> r.path("/imooc/commerce/login", "/imooc/commerce/register").uri("http://localhost:9001"))
                .build();
    }
}
