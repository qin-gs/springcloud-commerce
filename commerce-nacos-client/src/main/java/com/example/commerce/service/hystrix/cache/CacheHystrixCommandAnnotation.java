package com.example.commerce.service.hystrix.cache;

import com.example.commerce.service.NacosClientService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 使用注解开启 hystrix 请求缓存
 */
@Slf4j
@Service
public class CacheHystrixCommandAnnotation {

    private NacosClientService nacosClientService;

    public CacheHystrixCommandAnnotation(NacosClientService nacosClientService) {
        this.nacosClientService = nacosClientService;
    }

    /**
     * 01 -------------------------------------------------------------------------------------
     * 在 CacheResult 中指定 CacheKey
     */
    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(commandKey = "CacheHystrixCommandAnnotation")
    public List<ServiceInstance> useCacheByAnnotation1(String serviceId) {
        log.info("01. serviceId: {}", serviceId);
        return nacosClientService.getNacosInfo(serviceId);
    }

    public String getCacheKey(String cacheId) {
        return cacheId;
    }

    @CacheRemove(commandKey = "CacheHystrixCommandAnnotation", cacheKeyMethod = "getCacheKey")
    @HystrixCommand
    public void flushCacheByAnnotation1(String cacheId) {
        log.info("01. 清除缓存: {}", cacheId);
    }


    /**
     * 02 常用 -------------------------------------------------------------------------------------
     * CacheKey 放到方法参数上
     */
    @CacheResult
    @HystrixCommand(commandKey = "CacheHystrixCommandAnnotation")
    public List<ServiceInstance> useCacheByAnnotation2(@CacheKey String serviceId) {
        log.info("02. serviceId: {}", serviceId);
        return nacosClientService.getNacosInfo(serviceId);
    }

    @CacheRemove(commandKey = "CacheHystrixCommandAnnotation")
    @HystrixCommand
    public void flushCacheByAnnotation2(@CacheKey String cacheId) {
        log.info("02. 清除缓存: {}", cacheId);
    }


    /**
     * 03 -------------------------------------------------------------------------------------
     * 不指定 CacheKey 会使用所有的方法参数作为默认值
     */
    @CacheResult
    @HystrixCommand(commandKey = "CacheHystrixCommandAnnotation")
    public List<ServiceInstance> useCacheByAnnotation3(String serviceId) {
        log.info("03. serviceId: {}", serviceId);
        return nacosClientService.getNacosInfo(serviceId);
    }

    @CacheRemove(commandKey = "CacheHystrixCommandAnnotation")
    @HystrixCommand
    public void flushCacheByAnnotation3(String cacheId) {
        log.info("03. 清除缓存: {}", cacheId);
    }
}
