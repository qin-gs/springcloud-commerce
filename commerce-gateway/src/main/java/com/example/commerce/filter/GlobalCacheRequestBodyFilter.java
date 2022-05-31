package com.example.commerce.filter;

import com.example.commerce.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 定义一个全局过滤器：缓存请求登录请求 body
 */
@Slf4j
@Component
public class GlobalCacheRequestBodyFilter implements GlobalFilter, Ordered {
    /**
     * 将请求数据缓存，方便后端的过滤器读取
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 只处理注册 和 登录请求
        boolean should = exchange.getRequest().getURI().getPath().contains(GatewayConstant.LOGIN_URI)
                || exchange.getRequest().getURI().getPath().contains(GatewayConstant.REGISTER_URI);
        if (exchange.getRequest().getHeaders().getContentType() == null || !should) {
            return chain.filter(exchange);
        }
        // 获取 http 请求中的数据
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    // 确保数据缓冲区不被释放
                    DataBufferUtils.retain(dataBuffer);
                    // 获取当前请求数据的副本
                    Flux<DataBuffer> cacheFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                    // 重新包装 ServerHttpRequest，重写 getBody() 方法
                    ServerHttpRequest newRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cacheFlux;
                        }
                    };
                    // 将包装后的 request 继续传递
                    return chain.filter(exchange.mutate().request(newRequest).build());
                });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }
}
