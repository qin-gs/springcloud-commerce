package com.example.commerce.controller;

import com.example.commerce.service.NacosClientService;
import com.example.commerce.service.hystrix.*;
import com.example.commerce.service.hystrix.cache.CacheHystrixCommand;
import com.example.commerce.service.hystrix.cache.CacheHystrixCommandAnnotation;
import com.example.commerce.service.hystrix.merge.NacosClientCollapseCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    private final UseHystrixCommandAnnotation useHystrixCommandAnnotation;
    private final NacosClientService nacosClientService;
    private final CacheHystrixCommandAnnotation cacheHystrixCommandAnnotation;
    private final ObjectMapper MAPPER;

    public HystrixController(UseHystrixCommandAnnotation useHystrixCommandAnnotation, NacosClientService nacosClientService, CacheHystrixCommandAnnotation cacheHystrixCommandAnnotation, ObjectMapper mapper) {
        this.useHystrixCommandAnnotation = useHystrixCommandAnnotation;
        this.nacosClientService = nacosClientService;
        this.cacheHystrixCommandAnnotation = cacheHystrixCommandAnnotation;
        MAPPER = mapper;
    }

    @GetMapping("annotation-nacos-info")
    public List<ServiceInstance> getNacosInfo(@RequestParam String serviceId) {
        // 线程 http-nio-8000-exec-1
        log.info("getNacosInfo serviceId: {}, thread {}", serviceId, Thread.currentThread().getName());
        return useHystrixCommandAnnotation.getNacosInfo(serviceId);
    }

    @GetMapping("code-nacos-info")
    public List<ServiceInstance> getNacosInfoCode(@RequestParam String serviceId) throws ExecutionException,
            InterruptedException {
        log.info("getNacosInfo serviceId: {}, thread {}", serviceId, Thread.currentThread().getName());

        // 有 4 种方式调用
        // 1. 同步阻塞
        List<ServiceInstance> first = new NacosClientHystrixCommand(nacosClientService, serviceId).execute();
        log.info("execute first: {}, thread: {}", first, Thread.currentThread().getName());

        // 2. 异步非阻塞 (使用较多)
        Future<List<ServiceInstance>> secondFuture = new NacosClientHystrixCommand(nacosClientService, serviceId).queue();
        List<ServiceInstance> second = secondFuture.get();
        log.info("queue second: {}, thread: {}", second, Thread.currentThread().getName());

        // 3. 热响应调用
        Observable<List<ServiceInstance>> thirdObservable = new NacosClientHystrixCommand(nacosClientService, serviceId).observe();
        // 创建线程 调用 toBlocking 才会执行
        List<ServiceInstance> third = thirdObservable.toBlocking().single();
        log.info("observe third: {}, thread: {}", third, Thread.currentThread().getName());

        // 4. 异步冷响应调用
        Observable<List<ServiceInstance>> forthObservable = new NacosClientHystrixCommand(nacosClientService, serviceId).toObservable();
        // 调用 toBlocking 才会创建线程去执行
        List<ServiceInstance> forth = forthObservable.toBlocking().single();
        log.info("toObservable forth: {}, thread: {}", forth, Thread.currentThread().getName());

        return first;
    }

    @GetMapping("hystrix-observable-command")
    public List<ServiceInstance> getServiceInstanceObservable(@RequestParam String serviceId) {
        List<String> ids = Arrays.asList(serviceId, serviceId, serviceId);
        List<List<ServiceInstance>> result = new ArrayList<>(ids.size());

        NacosClientHystrixObservableCommand observableCommand = new NacosClientHystrixObservableCommand(nacosClientService, ids);
        Observable<List<ServiceInstance>> observe = observableCommand.observe();
        // 注册获取结果
        observe.subscribe(new Observer<List<ServiceInstance>>() {
            @Override
            public void onCompleted() {
                log.info("任务完成 serviceId: {}, thread: {}", serviceId, Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<ServiceInstance> serviceInstances) {
                result.add(serviceInstances);
            }
        });
        log.info("结果: {}, thread: {}", result, Thread.currentThread().getName());
        return result.get(0);
    }

    /**
     * 使用缓存
     */
    @GetMapping("cache-hystrix-command")
    public void cacheHystrixCommand(@RequestParam String serviceId) {
        // 使用缓存 command，发起两次请求
        CacheHystrixCommand command1 = new CacheHystrixCommand(nacosClientService, serviceId);
        CacheHystrixCommand command2 = new CacheHystrixCommand(nacosClientService, serviceId);

        List<ServiceInstance> result1 = command1.execute();
        // 第一次的结果会被缓存，第二次请求不会再次调用 run 方法
        List<ServiceInstance> result2 = command2.execute();

        log.info("result1: {}", result1);
        log.info("result2: {}", result2);

        CacheHystrixCommand.flushRequestCache(serviceId);
        log.info("清除缓存");

        CacheHystrixCommand command3 = new CacheHystrixCommand(nacosClientService, serviceId);
        CacheHystrixCommand command4 = new CacheHystrixCommand(nacosClientService, serviceId);

        List<ServiceInstance> result3 = command3.execute();
        // 第一次的结果会被缓存，第二次请求不会再次调用 run 方法
        List<ServiceInstance> result4 = command4.execute();

        log.info("result3: {}", result3);
        log.info("result4: {}", result4);
    }

    @GetMapping("cache-anno-1")
    public List<ServiceInstance> getCacheByAnno1(@RequestParam String serviceId) throws JsonProcessingException {
        log.info("测试缓存注解 1: {}", serviceId);
        List<ServiceInstance> result1 = cacheHystrixCommandAnnotation.useCacheByAnnotation1(serviceId);
        List<ServiceInstance> result2 = cacheHystrixCommandAnnotation.useCacheByAnnotation1(serviceId);

        log.info("1. result1: {}", MAPPER.writeValueAsString(result1));
        log.info("1. result2: {}", MAPPER.writeValueAsString(result2));

        cacheHystrixCommandAnnotation.flushCacheByAnnotation1(serviceId);

        List<ServiceInstance> result3 = cacheHystrixCommandAnnotation.useCacheByAnnotation1(serviceId);
        List<ServiceInstance> result4 = cacheHystrixCommandAnnotation.useCacheByAnnotation1(serviceId);
        log.info("1. result3: {}", MAPPER.writeValueAsString(result3));
        log.info("1. result4: {}", MAPPER.writeValueAsString(result4));

        return result4;
    }

    @GetMapping("cache-anno-2")
    public List<ServiceInstance> getCacheByAnno2(@RequestParam String serviceId) throws JsonProcessingException {
        log.info("测试缓存注解 2: {}", serviceId);
        List<ServiceInstance> result1 = cacheHystrixCommandAnnotation.useCacheByAnnotation2(serviceId);
        List<ServiceInstance> result2 = cacheHystrixCommandAnnotation.useCacheByAnnotation2(serviceId);

        log.info("2. result1: {}", MAPPER.writeValueAsString(result1));
        log.info("2. result2: {}", MAPPER.writeValueAsString(result2));

        cacheHystrixCommandAnnotation.flushCacheByAnnotation2(serviceId);

        List<ServiceInstance> result3 = cacheHystrixCommandAnnotation.useCacheByAnnotation2(serviceId);
        List<ServiceInstance> result4 = cacheHystrixCommandAnnotation.useCacheByAnnotation2(serviceId);
        log.info("2. result3: {}", MAPPER.writeValueAsString(result3));
        log.info("2. result4: {}", MAPPER.writeValueAsString(result4));

        return result4;
    }

    @GetMapping("cache-anno-3")
    public List<ServiceInstance> getCacheByAnno3(@RequestParam String serviceId) throws JsonProcessingException {
        log.info("测试缓存注解 2: {}", serviceId);
        List<ServiceInstance> result1 = cacheHystrixCommandAnnotation.useCacheByAnnotation3(serviceId);
        List<ServiceInstance> result2 = cacheHystrixCommandAnnotation.useCacheByAnnotation3(serviceId);

        log.info("3. result1: {}", MAPPER.writeValueAsString(result1));
        log.info("3. result2: {}", MAPPER.writeValueAsString(result2));

        cacheHystrixCommandAnnotation.flushCacheByAnnotation3(serviceId);

        List<ServiceInstance> result3 = cacheHystrixCommandAnnotation.useCacheByAnnotation3(serviceId);
        List<ServiceInstance> result4 = cacheHystrixCommandAnnotation.useCacheByAnnotation3(serviceId);
        log.info("3. result3: {}", MAPPER.writeValueAsString(result3));
        log.info("3. result4: {}", MAPPER.writeValueAsString(result4));

        return result4;
    }

    /**
     * 用代码完成请求合并
     */
    @GetMapping("request-merge-code")
    public void requestMerge() throws Exception {
        // 三个请求会被合并
        NacosClientCollapseCommand command1 = new NacosClientCollapseCommand(nacosClientService, "commerce-nacos-client1");
        NacosClientCollapseCommand command2 = new NacosClientCollapseCommand(nacosClientService, "commerce-nacos-client2");
        NacosClientCollapseCommand command3 = new NacosClientCollapseCommand(nacosClientService, "commerce-nacos-client3");

        // 这里三个请求必须先 queue 然后 get，否则不会合并
        Future<List<ServiceInstance>> future1 = command1.queue();
        Future<List<ServiceInstance>> future2 = command2.queue();
        Future<List<ServiceInstance>> future3 = command3.queue();
        log.info("result1: {}", MAPPER.writeValueAsString(future1.get()));
        log.info("result2: {}", MAPPER.writeValueAsString(future2.get()));
        log.info("result3: {}", MAPPER.writeValueAsString(future3.get()));

        // 过了合并的事件窗口不会被合并
        TimeUnit.SECONDS.sleep(3);

        NacosClientCollapseCommand command4 = new NacosClientCollapseCommand(nacosClientService, "commerce-nacos-client4");
        List<ServiceInstance> result4 = command4.queue().get();

        log.info("result4: {}", MAPPER.writeValueAsString(result4));

    }

    /**
     * 用注解完成请求合并
     */
    @GetMapping("request-merge-anno")
    public void requestMergeAnno() throws Exception {
        Future<List<ServiceInstance>> future1 = nacosClientService.findNacosClientInfo("commerce-nacos-client1");
        Future<List<ServiceInstance>> future2 = nacosClientService.findNacosClientInfo("commerce-nacos-client2");
        Future<List<ServiceInstance>> future3 = nacosClientService.findNacosClientInfo("commerce-nacos-client3");
        List<ServiceInstance> result1 = future1.get();
        List<ServiceInstance> result2 = future2.get();
        List<ServiceInstance> result3 = future3.get();
        log.info("result1: {}", MAPPER.writeValueAsString(result1));
        log.info("result2: {}", MAPPER.writeValueAsString(result2));
        log.info("result3: {}", MAPPER.writeValueAsString(result3));

        TimeUnit.SECONDS.sleep(3);

        Future<List<ServiceInstance>> future4 = nacosClientService.findNacosClientInfo("commerce-nacos-client4");
        List<ServiceInstance> result4 = future4.get();
        log.info("result4: {}", MAPPER.writeValueAsString(result4));

    }
}
