package com.example.commerce.controller;

import com.example.commerce.feign.SentinelFeignClient;
import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * sentinel + openFeign 实现熔断降级
 */
@Slf4j
@RestController
@RequestMapping("sentinel-feign")
public class SentinelFeignController {

    private SentinelFeignClient sentinelFeignClient;

    public SentinelFeignController(SentinelFeignClient sentinelFeignClient) {
        this.sentinelFeignClient = sentinelFeignClient;
    }

    /**
     * 通过 feign 接口获取结果
     */
    @GetMapping("getResultByFeign")
    public CommonResponse<String> getResultByFeign(@RequestParam Integer code) {
        return sentinelFeignClient.getResultByFeign(code);
    }
}
