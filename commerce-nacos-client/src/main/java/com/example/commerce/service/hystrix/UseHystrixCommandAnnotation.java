package com.example.commerce.service.hystrix;

import com.example.commerce.service.NacosClientService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 使用 hystrix 注解
 */
@Slf4j
@Service
public class UseHystrixCommandAnnotation {

    private final NacosClientService nacosClientService;

    public UseHystrixCommandAnnotation(NacosClientService nacosClientService) {
        this.nacosClientService = nacosClientService;
    }

    @HystrixCommand(
            // 对 hystrix 命令进行分组，便于统计，默认为类名
            groupKey = "NacosClientService",
            // 对 hystrix 命令进行分组，便于统计，默认为方法名
            commandKey = "NacosClientService",
            // 舱壁模式
            threadPoolKey = "NacosClientService",
            // 后备模式
            fallbackMethod = "fallbackMethod",
            // 断路器模式
            commandProperties = {
                    // 超时时间
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
                    // 熔断的最少请求次数，达到该值后才会计算成功率
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                    // 熔断的阈值，如果 50% 的请求失败会触发熔断
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
            },
            // 舱壁模式的线程池配置
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "10"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    // 时间窗口中搜集统计信息的次数 (1440ms 内统计 12 次)
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")
            }
    )
    public List<ServiceInstance> getNacosInfo(String serviceId) {

        // 线程 hystrix-NacosClientService-1
        log.info("hystrix annotation serviceId: {}, thread: {}", serviceId, Thread.currentThread().getName());
        // 测试超时熔断
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return nacosClientService.getNacosInfo(serviceId);
    }

    /**
     * 后备模式：在同一个类，有相同的方法签名
     */
    public List<ServiceInstance> fallbackMethod(String serviceId) {
        // 线程 HystrixTimer-1
        log.warn("hystrix annotation fallback serviceId: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return Collections.emptyList();
    }
}
