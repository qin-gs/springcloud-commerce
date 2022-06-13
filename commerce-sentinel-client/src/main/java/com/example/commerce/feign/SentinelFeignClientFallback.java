package com.example.commerce.feign;

import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * sentinel 对 openFeign 接口的降级策略
 */
@Slf4j
@Component
public class SentinelFeignClientFallback implements SentinelFeignClient {

    @Override
    public CommonResponse<String> getResultByFeign(Integer code) {

        log.error("openFeign 调用失败，进入降级策略");
        return new CommonResponse<>(-1, "openFeign 调用失败，进入降级策略", "");
    }
}
