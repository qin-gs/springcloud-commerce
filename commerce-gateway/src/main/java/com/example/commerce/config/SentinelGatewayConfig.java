package com.example.commerce.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * gateway + sentinel 实现限流
 */
@Slf4j
@Configuration
public class SentinelGatewayConfig {

    private List<ViewResolver> viewResolvers;
    /**
     * http 请求和响应数据的编解码配置
     */
    private ServerCodecConfigurer serverCodecConfigurer;

    public SentinelGatewayConfig(ObjectProvider<List<ViewResolver>> objectProvider, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = objectProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 默认的限流异常处理器，限流出现异常时会执行这个 handler
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // 默认会返回 429 和 限流信息
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /**
     * gateway 的全局限流过滤器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * 初始化限流规则
     */
    // @PostConstruct
    public void initRule() {
        log.info("初始化限流规则");
        initGatewayRules();
        log.info("初始化自定义限流异常处理器");
        initBlockExceptionHandler();
    }

    /**
     * 网关限流规则
     */
    private void initGatewayRules() {
        log.info("网关 硬编码限流规则");
        Set<GatewayFlowRule> rules = new HashSet<>();

        GatewayFlowRule rule = new GatewayFlowRule();
        // 限流模式 (根据 routeId 进行限流)
        rule.setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID);
        // 指定限流的资源 (微服务名称 service-id，该服务下的所有接口 60 内最多访问 3 次)
        rule.setResource("commerce-nacos-client");
        // 限制 qps
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 窗口 和 限流阈值
        rule.setIntervalSec(60);
        rule.setCount(3);

        // 加载到网关中
        // rules.add(rule);

        // 限流分组 (规则，分组)
        rules.add(new GatewayFlowRule("nacos-client-api-1")
                .setCount(3).setIntervalSec(60));
        rules.add(new GatewayFlowRule("nacos-client-api-2")
                .setCount(1).setIntervalSec(60));

        GatewayRuleManager.loadRules(rules);

        // 加载限流分组
        initCustomizedApis();
    }

    /**
     * 硬编码网关限流分组
     */
    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();

        // 分组 "nacos-client-api"，限制服务中的所有接口 (范围太大了，不建议使用)
        // HashSet<ApiPredicateItem> items1 = new HashSet<>();
        // items1.add(
        //         new ApiPathPredicateItem()
        //                 // 模糊匹配 (前缀)
        //                 .setPattern("/imooc/commerce-nacos-client/**")
        //                 .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
        // );
        // ApiDefinition api = new ApiDefinition("nacos-client-api")
        //         .setPredicateItems(items1);


        // 分组 1
        ApiDefinition api1 = new ApiDefinition("nacos-client-api-1")
                .setPredicateItems(Collections.singleton(new ApiPathPredicateItem()
                        // 精确匹配
                        .setPattern("/imooc/commerce-nacos-client/nacos-client/service-instance")
                ));

        // 分组 2
        ApiDefinition api2 = new ApiDefinition("nacos-client-api-2")
                .setPredicateItems(Collections.singleton(new ApiPathPredicateItem()
                        // 精确匹配
                        .setPattern("/imooc/commerce-nacos-client/nacos-client/rest-template")
                ));

        definitions.add(api1);
        definitions.add(api2);

        // 加载限流分组
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    /**
     * 自定义限流异常处理器
     */
    private void initBlockExceptionHandler() {
        BlockRequestHandler handler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
                log.error("自定义限流异常处理器：{}", t.getMessage());

                Map<String, String> result = new HashMap<>(4);
                result.put("code", String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()));
                result.put("message", "系统繁忙，请稍后再试 " + HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
                result.put("route", "commerce-nacos-client");

                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(result));
            }
        };
        // 注册限流异常处理器
        GatewayCallbackManager.setBlockHandler(handler);
    }

}
