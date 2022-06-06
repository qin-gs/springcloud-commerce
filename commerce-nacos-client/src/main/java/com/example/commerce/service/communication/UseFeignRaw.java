package com.example.commerce.service.communication;

import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Random;

/**
 * 使用 Feign 的原生 api；
 * OpenFeign = Ribbon + Feign
 */
@Slf4j
@Service
public class UseFeignRaw {
    /**
     * 服务发现
     */
    private final DiscoveryClient discoveryClient;

    public UseFeignRaw(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 使用原生的 Feign api 调用远程服务
     * 默认初始化；设置自定义配置；生成代理对象
     */
    public JwtToken feignRaw(UsernameAndPassword usernameAndPassword) {
        // 获取服务列表；
        String serviceId = null;
        Annotation[] annotations = AuthorityFeignClient.class.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(FeignClient.class)) {
                serviceId = ((FeignClient) annotation).value();
                log.info("服务id serviceId: {}", serviceId);
                break;
            }
        }
        if (serviceId == null) {
            throw new RuntimeException("服务 id 找不到");
        }
        // 获取服务实例
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (CollectionUtils.isEmpty(instances)) {
            throw new RuntimeException("服务实例找不到");
        }

        // 选择服务实例
        ServiceInstance instance = instances.get(new Random().nextInt(instances.size()));
        log.info("选择服务实例：{}, host：{}, port: {}", instance, instance.getHost(), instance.getPort());

        // feign 客户端初始化，自定义配置
        AuthorityFeignClient target = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logLevel(Logger.Level.FULL)
                .contract(new SpringMvcContract())
                // 生成代理类
                .target(AuthorityFeignClient.class, String.format("http://%s:%s", instance.getHost(),
                        instance.getPort()));
        return target.getTokenByFeign(usernameAndPassword);

    }
}
