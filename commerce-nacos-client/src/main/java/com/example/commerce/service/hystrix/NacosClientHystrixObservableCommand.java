package com.example.commerce.service.hystrix;

import com.example.commerce.service.NacosClientService;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import rx.Observable;
import rx.Subscriber;

import java.util.Collections;
import java.util.List;

/**
 * 基于信号量的隔离策略实现舱壁模式；使用应用服务的父线程调用 contract；
 * 可以依次返回多条数据
 */
@Slf4j
public class NacosClientHystrixObservableCommand extends HystrixObservableCommand<List<ServiceInstance>> {

    private final NacosClientService nacosClientService;
    private final List<String> serviceIds;

    public NacosClientHystrixObservableCommand(NacosClientService nacosClientService, List<String> serviceIds) {
        super(
                HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("NacosClientHystrixObservableCommand"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("NacosClientHystrixObservableCommand"))
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter()
                                        // 开启降级
                                        .withFallbackEnabled(true)
                                        // 开启熔断
                                        .withCircuitBreakerEnabled(true)
                        )
        );
        this.nacosClientService = nacosClientService;
        this.serviceIds = serviceIds;
    }

    /**
     * 要保护的方法
     */
    @Override
    protected Observable<List<ServiceInstance>> construct() {
        return Observable.create(new Observable.OnSubscribe<List<ServiceInstance>>() {
            @Override
            public void call(Subscriber<? super List<ServiceInstance>> subscriber) {
                // 三个事件方法 onNext, onCompleted, onError
                try {
                    if (!subscriber.isUnsubscribed()) {
                        log.info("subscriber serviceId: {}, thread: {}", serviceIds, Thread.currentThread().getName());
                        serviceIds.stream().map(nacosClientService::getNacosInfo).forEach(subscriber::onNext);
                        subscriber.onCompleted();
                        log.info("任务完成 serviceId: {}, thread: {}", serviceIds, Thread.currentThread().getName());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 服务降级
     */
    @Override
    protected Observable<List<ServiceInstance>> resumeWithFallback() {
        return Observable.create(new Observable.OnSubscribe<List<ServiceInstance>>() {
            @Override
            public void call(Subscriber<? super List<ServiceInstance>> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        log.info("(fallback) serviceId: {}, thread: {}", serviceIds, Thread.currentThread().getName());
                        subscriber.onNext(Collections.emptyList());
                        subscriber.onCompleted();
                        log.info("(fallback) 任务完成 serviceId: {}, thread: {}", serviceIds, Thread.currentThread().getName());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
