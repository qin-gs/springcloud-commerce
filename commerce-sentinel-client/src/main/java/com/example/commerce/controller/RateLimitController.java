package com.example.commerce.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.commerce.handler.MyBlockHandler;
import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于控制台设置流量控制规则
 */
@Slf4j
@RestController
@RequestMapping("dashboard")
public class RateLimitController {

    /**
     * 在 dashboard 的 流控规则 中按照资源名称新增流控规则
     */
    @GetMapping("by-resource")
    @SentinelResource(value = "byResource",
            blockHandler = "blockHandler",
            blockHandlerClass = MyBlockHandler.class)
    public CommonResponse<String> byResource() {
        log.info("dashboard 流量控制正常返回");
        return new CommonResponse<>(0, "dashboard 流量控制正常返回", "resource");
    }

    /**
     * 在 簇点链路 中添加流控规则
     */
    @GetMapping("by-url")
    @SentinelResource(value = "byUrl")
    public CommonResponse<String> byUrl() {
        log.info("dashboard 流量控制正常返回");
        return new CommonResponse<>(0, "dashboard 流量控制正常返回", "url");
    }
}
