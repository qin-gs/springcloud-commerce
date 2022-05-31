package com.example.commerce.service;

import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 使用代码查看 sleuth 生成的跟踪信息
 */
@Slf4j
@Service
public class SleuthTraceInfoService {

    /**
     * 跟踪对象
     */
    private final Tracer tracer;

    public SleuthTraceInfoService(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * 打印跟踪信息
     */
    public void logCurrentTraceInfo() {
        log.info("sleuth trace id: {}", tracer.currentSpan().context().traceIdString());
        log.info("sleuth span id: {}", tracer.currentSpan().context().spanId());
    }
}
