package com.example.commerce.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 通过 nacos 下发的动态路由配置，监听 nacos 中的配置变更，并实时更新到 DynamicRouteServiceImpl 中
 */
@Slf4j
@Service
@DependsOn("gatewayConfig")
public class DynamicRouteServiceImplByNacos {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * nacos 配置服务客户端
     */
    private ConfigService configService;
    private final DynamicRouteServiceImpl dynamicRouteService;

    public DynamicRouteServiceImplByNacos(DynamicRouteServiceImpl dynamicRouteService) {
        this.dynamicRouteService = dynamicRouteService;
    }

    /**
     * 连接到 nacos 之后，初始化默认配置
     */
    @PostConstruct
    public void init() {
        log.info("init gateway nacos config service");
        try {
            // 初始化 nacos 客户端
            configService = initConfigService();
            if (configService == null) {
                log.error("init gateway nacos config service error");
                return;
            }
            // 通过 nacos config 指定路由配置路径去获取路由配置
            String config = configService.getConfig(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, GatewayConfig.DEFAULT_TIMEOUT);
            List<RouteDefinition> routeDefinitions = MAPPER.readValue(config, new TypeReference<List<RouteDefinition>>() {
            });

            if (!routeDefinitions.isEmpty()) {
                routeDefinitions.forEach(routeDefinition -> {
                    log.info("init gateway nacos route definition {}", routeDefinition);
                    dynamicRouteService.addRouteDefinition(routeDefinition);
                });
            }

        } catch (NacosException e) {
            log.error("init gateway nacos error {}", e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // 设置监听器 (监听之后的变化)
        dynamicRouteByNacosListener(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP);
    }

    /**
     * 初始化
     */
    private ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);

            return configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            log.error("init gateway nacos error {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 监听 nacos 下发的动态路由配置信息
     */
    private void dynamicRouteByNacosListener(String dataId, String group) {
        try {
            // 给 nacos 客户端添加监听器 (监听到配置更改同步到 gateway 中，实现动态路由效果)
            configService.addListener(dataId, group, new Listener() {
                /**
                 * 可以自己提供线程池 (不提供就使用默认线程池)
                 */
                @Override
                public Executor getExecutor() {
                    return null;
                }

                /**
                 * 收到配置变更信息
                 */
                @Override
                public void receiveConfigInfo(String configInfo) {
                    try {
                        log.info("receive configInfo {}", configInfo);
                        List<RouteDefinition> definitions = MAPPER.readValue(configInfo, new TypeReference<List<RouteDefinition>>() {
                        });
                        dynamicRouteService.updateList(definitions);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (NacosException e) {
            log.error("dynamicRouteByNacosListener error {}", e.getMessage(), e);
        }
    }
}
