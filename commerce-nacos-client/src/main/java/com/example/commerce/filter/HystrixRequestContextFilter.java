package com.example.commerce.filter;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 初始化 hystrix 上下文环境
 */
@Slf4j
@Component
@WebFilter(filterName = "hystrixRequestContextFilter", urlPatterns = "/*", asyncSupported = true)
public class HystrixRequestContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 初始化 hystrix 请求上下文
        HystrixRequestContext hystrixRequestContext = HystrixRequestContext.initializeContext();
        try {
            // 引入 sleuth 之后需要修改一些配置
            hystrixConcurrencyStrategy();
            chain.doFilter(request, response);
        } finally {
            // 销毁 hystrix 请求上下文
            hystrixRequestContext.shutdown();
        }
    }

    public void hystrixConcurrencyStrategy() {
        try {
            HystrixConcurrencyStrategy target = HystrixConcurrencyStrategyDefault.getInstance();
            HystrixConcurrencyStrategy current = HystrixPlugins.getInstance().getConcurrencyStrategy();
            // 判断是不是自己想要的
            if (current instanceof HystrixConcurrencyStrategyDefault) {
                return;
            }
            // 不是的话需要修改，将其它配置保存一下
            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();

            // 重置配置，然后将之前的写回去
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(target);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);

            log.info("初始化 hystrix 并发策略完成");
        } catch (Exception e) {
            log.error("初始化 hystrix 并发策略失败 ", e);
        }
    }
}
