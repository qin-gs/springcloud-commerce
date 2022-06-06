package com.example.commerce.service.hystrix;

import com.example.commerce.service.NacosClientService;
import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;

/**
 * 给 NacosClientService 实现包装；
 * 舱壁模式：隔离策略 线程池，信号量
 * 有自己独立的线程池，在不同的线程中执行，只能返回单条数据
 */
@Slf4j
public class NacosClientHystrixCommand extends HystrixCommand<List<ServiceInstance>> {

    private final NacosClientService nacosClientService;
    private final String serviceId;

    public NacosClientHystrixCommand(NacosClientService nacosClientService, String serviceId) {
        // groupKey 对 hystrix 命令进行分组，便于监控统计，默认为类名
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("NacosClientService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("NacosClientHystrixCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("NacosClientPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        // 线程池隔离策略
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                        // 开启降级
                        .withFallbackEnabled(true)
                        // 开启熔断器
                        .withCircuitBreakerEnabled(true))
        );

        // 配置信号量隔离策略
        // super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("NacosClientService"))
        //         .andCommandKey(HystrixCommandKey.Factory.asKey("NacosClientHystrixCommand"))
        //         .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        //                 .withCircuitBreakerRequestVolumeThreshold(10)
        //                 .withCircuitBreakerSleepWindowInMilliseconds(5000)
        //                 .withCircuitBreakerErrorThresholdPercentage(50)
        //                 // 使用信号量隔离策略
        //                 .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));

        this.nacosClientService = nacosClientService;
        this.serviceId = serviceId;
    }

    /**
     * 要保护的方法
     */
    @Override
    protected List<ServiceInstance> run() throws Exception {
        log.info("serviceId: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return nacosClientService.getNacosInfo(serviceId);
    }

    /**
     * 降级处理
     */
    @Override
    protected List<ServiceInstance> getFallback() {
        log.warn("NacosClientService error, serviceId: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return Collections.emptyList();
    }
}
