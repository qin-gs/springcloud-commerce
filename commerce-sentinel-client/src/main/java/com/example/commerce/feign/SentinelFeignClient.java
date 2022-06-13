package com.example.commerce.feign;

import com.example.commerce.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 通过 sentinel 对 openFeign 实现熔断降级
 */
@FeignClient(value = "commerce-imooc", fallback = SentinelFeignClientFallback.class)
public interface SentinelFeignClient {

    @RequestMapping(value = "getResultByFeign", method = RequestMethod.GET)
    CommonResponse<String> getResultByFeign(@RequestParam Integer code);

}
