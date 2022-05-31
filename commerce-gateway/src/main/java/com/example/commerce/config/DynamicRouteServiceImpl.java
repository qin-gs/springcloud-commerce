package com.example.commerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 网关事件监听器
 * 事件推送：动态更新路由网关
 */
@Slf4j
@Service
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {
    /**
     * 写路由定义
     */
    private final RouteDefinitionWriter routeDefinitionWriter;
    /**
     * 获取路由定义
     */
    private final RouteDefinitionLocator routeDefinitionLocator;
    /**
     * 事件发布
     */
    private ApplicationEventPublisher publisher;

    public DynamicRouteServiceImpl(RouteDefinitionWriter routeDefinitionWriter, RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // 完成事件推送句柄初始化
        this.publisher = applicationEventPublisher;
    }

    /**
     * 增加路由定义
     */
    public String addRouteDefinition(RouteDefinition definition) {
        try {
            log.info("gateway add route: {}", definition);
            // 保存路由配置并发布
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            // 发布事件通知给 gateway，同步新增路由定义
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            log.error("addRouteDefinition error", e);
            return "error";
        }
    }

    public String updateList(List<RouteDefinition> definitions) {
        log.info("gateway update route list: {}", definitions);
        List<RouteDefinition> routeDefinitions = this.routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (!CollectionUtils.isEmpty(routeDefinitions)) {
            // 清除之前所有的路由配置
            routeDefinitions.forEach(routeDefinition -> {
                this.deleteById(routeDefinition.getId());
            });
        }
        // 更新路由定义同步到 gateway
        definitions.forEach(this::addRouteDefinition);
        return "success";
    }

    /**
     * 更新路由定义
     */
    public String updateRouteDefinition(RouteDefinition definition) {
        try {
            log.info("gateway update route: {}", definition);
            // 保存路由配置
            routeDefinitionWriter.delete(Mono.just(definition.getId()));
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            // 发布事件通知给 gateway，同步更新路由定义
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            log.error("updateRouteDefinition error id {} {}", definition.getId(), e);
            return "error " + definition.getId();
        }
    }

    /**
     * 删除路由定义
     */
    public String deleteById(String id) {
        try {
            log.info("gateway delete route: {}", id);
            // 删除路由配置并发布
            routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            // 发布事件通知给 gateway，同步删除路由定义
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            log.error("deleteRouteDefinition error", e);
            return "error";
        }
    }
}
