package com.example.commerce.feign;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * feign 调用时把 header 也传递到服务提供方
 */
@Slf4j
@Configuration
public class FeignConfig {

    /**
     * 给 feign 加上请求拦截器，把 header 也传递到服务提供方
     */
    @Bean
    public RequestInterceptor headerInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String values = request.getHeader(name);
                        // 不能传递 content-length
                        // 请求可能一直无法返回 或 请求响应数据被截断
                        if (!"content-length".equalsIgnoreCase(name)) {
                            requestTemplate.header(name, values);
                        }
                    }
                }
            }
        };
    }
}
