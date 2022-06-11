package com.example.commerce.conf;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 开启服务之间的调用保护，对 RestTemplate 进行配置
 */
@Slf4j
@Configuration
public class SentinelConfig {

    /**
     * 包装 RestTemplate，使其具备 Sentinel 的功能
     */
    @Bean
    @SentinelRestTemplate(
            blockHandler = "blockHandler", blockHandlerClass = RestTemplateExceptionUtil.class,
            fallback = "fallback", fallbackClass = RestTemplateExceptionUtil.class)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
