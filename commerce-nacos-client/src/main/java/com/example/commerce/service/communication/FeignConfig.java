package com.example.commerce.service.communication;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * feign 配置类
 */
@Configuration
public class FeignConfig {

    /**
     * 日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 重试策略
     */
    @Bean
    public Retryer feignRetryer() {
        // period: 发起请求时间间隔
        // maxPeriod: 最大重试时间间隔 (min-max 之间随机选择一个时间发起请求)
        // maxAttempts: 最大重试次数
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 5);
    }

    public static final int CONNECT_TIMEOUT_MILLIS = 5000;
    public static final int READ_TIMEOUT_MILLIS = 5000;

    /**
     * 限制请求连接 和 响应时间
     */
    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(
                CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS,
                READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS,
                true);
    }

}
