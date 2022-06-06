package com.example.commerce.controller;

import com.example.commerce.service.NacosClientService;
import com.example.commerce.service.hystrix.NacosClientHystrixCommand;
import com.example.commerce.service.hystrix.NacosClientHystrixObservableCommand;
import com.example.commerce.service.hystrix.UseHystrixCommandAnnotation;
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

@Slf4j
@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    private final UseHystrixCommandAnnotation useHystrixCommandAnnotation;
    private final NacosClientService nacosClientService;

    public HystrixController(UseHystrixCommandAnnotation useHystrixCommandAnnotation, NacosClientService nacosClientService) {
        this.useHystrixCommandAnnotation = useHystrixCommandAnnotation;
        this.nacosClientService = nacosClientService;
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
}
