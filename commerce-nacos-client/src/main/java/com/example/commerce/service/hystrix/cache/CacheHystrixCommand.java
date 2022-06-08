package com.example.commerce.service.hystrix.cache;

import com.example.commerce.service.NacosClientService;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;

/**
 * 带有缓存功能的 hystrix
 */
@Slf4j
public class CacheHystrixCommand extends HystrixCommand<List<ServiceInstance>> {

    /**
     * 需要保护的服务
     */
    private final NacosClientService nacosClientService;
    /**
     * 服务 id
     */
    private final String serviceId;

    private static final HystrixCommandKey COMMAND_KEY = HystrixCommandKey.Factory.asKey("CacheHystrixCommand");

    public CacheHystrixCommand(NacosClientService nacosClientService, String serviceId) {
        super(
                HystrixCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("CacheHystrixCommandGroup"))
                        .andCommandKey(COMMAND_KEY)
        );
        this.nacosClientService = nacosClientService;
        this.serviceId = serviceId;
    }

    @Override
    protected List<ServiceInstance> run() throws Exception {
        log.info("服务id: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return nacosClientService.getNacosInfo(serviceId);
    }

    @Override
    protected List<ServiceInstance> getFallback() {
        log.warn("(fallback) getFallback, thread: {}", Thread.currentThread().getName());
        return Collections.emptyList();
    }

    @Override
    protected String getCacheKey() {
        return serviceId;
    }

    /**
     * 清除缓存
     */
    public static void flushRequestCache(String serviceId) {
        HystrixRequestCache.getInstance(COMMAND_KEY, HystrixConcurrencyStrategyDefault.getInstance()).clear(serviceId);
        log.info("清除缓存, serviceId: {}, thread: {}", serviceId, Thread.currentThread().getName());
    }
}
