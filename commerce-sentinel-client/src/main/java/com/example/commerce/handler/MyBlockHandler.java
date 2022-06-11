package com.example.commerce.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyBlockHandler {

    /**
     * 抛出限流异常时，指定调用的方法 (必须是 static)
     */
    public static CommonResponse<String> blockHandler(BlockException ex) {
        log.error("限流测试出现错误: {}", ex.getRule(), ex);
        return new CommonResponse<>(-1, "限流测试出现错误", "");
    }
}
