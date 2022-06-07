package com.example.commerce.service.hystrix.merge;

import com.example.commerce.service.NacosClientService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;

@Slf4j
public class NacosClientBatchCommand extends HystrixCommand<List<List<ServiceInstance>>> {

    private NacosClientService nacosClientService;
    private List<String> serviceIds;

    protected NacosClientBatchCommand(NacosClientService nacosClientService, List<String> serviceIds) {
        super(
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("NacosClientBatchCommand"))
        );
        this.nacosClientService = nacosClientService;
        this.serviceIds = serviceIds;
    }

    @Override
    protected List<List<ServiceInstance>> run() throws Exception {
        log.info("批量获取: {}", serviceIds);
        return nacosClientService.getNacosClientInfos(serviceIds);
    }

    @Override
    protected List<List<ServiceInstance>> getFallback() {
        log.info("(fallback) NacosClientBatchCommand");
        return Collections.emptyList();
    }
}
