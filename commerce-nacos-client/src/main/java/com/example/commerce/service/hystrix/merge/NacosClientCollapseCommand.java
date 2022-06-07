package com.example.commerce.service.hystrix.merge;

import com.example.commerce.service.NacosClientService;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合并请求
 */
@Slf4j
public class NacosClientCollapseCommand extends HystrixCollapser<List<List<ServiceInstance>>, List<ServiceInstance>, String> {

    private NacosClientService nacosClientService;
    private String serviceId;

    public NacosClientCollapseCommand(NacosClientService nacosClientService, String serviceId) {
        super(
                HystrixCollapser.Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("NacosClientCollapseCommand"))
                        .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(200))
        );
        this.nacosClientService = nacosClientService;
        this.serviceId = serviceId;
    }

    /**
     * 请求参数
     */
    @Override
    public String getRequestArgument() {
        return this.serviceId;
    }

    /**
     * 创建批量请求 hystrix command
     */
    @Override
    protected HystrixCommand<List<List<ServiceInstance>>> createCommand(Collection<CollapsedRequest<List<ServiceInstance>, String>> collapsedRequests) {

        // 获取请求参数
        List<String> serviceIds = collapsedRequests.stream()
                .map(CollapsedRequest::getArgument)
                .collect(Collectors.toList());
        return new NacosClientBatchCommand(nacosClientService, serviceIds);
    }

    /**
     * 将响应分发给单独的请求
     */
    @Override
    protected void mapResponseToRequests(List<List<ServiceInstance>> batchResponse,
                                         Collection<CollapsedRequest<List<ServiceInstance>, String>> collapsedRequests) {
        int index = 0;
        for (CollapsedRequest<List<ServiceInstance>, String> collapsedRequest : collapsedRequests) {
            // 从批量响应集合中按顺序取出结果
            List<ServiceInstance> serviceInstances = batchResponse.get(index++);
            // 将结果放入响应
            collapsedRequest.setResponse(serviceInstances);
        }
    }
}
