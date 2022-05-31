package com.example.commerce.filter;

import com.example.commerce.constant.CommonConstant;
import com.example.commerce.constant.GatewayConstant;
import com.example.commerce.util.TokenParseUtil;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.LoginUserInfo;
import com.example.commerce.vo.UsernameAndPassword;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 全局过滤器(网关层面进行鉴权)：登录，鉴权
 */
@Slf4j
@Component
public class GlobalLoginOrRegisterFilter implements GlobalFilter, Ordered {

    private final ObjectMapper MAPPER = new ObjectMapper();
    private final RestTemplate restTemplate;
    /**
     * 注册中心客户端，从注册中心获取服务实例信息
     */
    private final LoadBalancerClient loadBalancerClient;

    public GlobalLoginOrRegisterFilter(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
        this.restTemplate = restTemplate;
        this.loadBalancerClient = loadBalancerClient;
    }

    /**
     * 全局过滤器：登录，注册，鉴权；
     * 1. 登录 或 注册，去授权中心拿到 token 并返回
     * 2. 其它服务需要鉴权 (401)
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 登录
        if (request.getURI().getPath().contains(GatewayConstant.LOGIN_URI)) {
            // 去授权中心获取 token
            String token = getTokenFromAuthorityCenter(request, GatewayConstant.AUTHORITY_CENTER_TOKEN_URI_FORMAT);
            response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, token == null ? "null" : token);
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        // 注册
        if (request.getURI().getPath().contains(GatewayConstant.REGISTER_URI)) {
            // 去授权中心创建一个用户，返回token
            String token = getTokenFromAuthorityCenter(request, GatewayConstant.REGISTER_URI_FORMAT);
            response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, token == null ? "null" : token);
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        // 访问其它服务需要鉴权，校验 token，判断是否登录
        String token = request.getHeaders().getFirst(CommonConstant.JWT_USER_INFO_KEY);

        LoginUserInfo userInfo = null;
        try {
            userInfo = TokenParseUtil.parseUserInfoFromToken(token);
        } catch (Exception e) {
            log.error("parse token failed {}", token, e);
        }
        if (userInfo == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    /**
     * 从 post 请求中获取到数据
     *
     * @param request 这就是上一个过滤器缓存的对象
     */
    private String parseBodyFromRequest(ServerHttpRequest request) {
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        // 订阅缓冲区，消费请求体中的数据
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            // 释放 buffer，因为上一个过滤器使用 retain 方法保留了 buffer
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }

    /**
     * 从授权中心获取 token
     */
    private String getTokenFromAuthorityCenter(ServerHttpRequest request, String uriFormat) {
        try {
            // 获取 service
            ServiceInstance service = loadBalancerClient.choose(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
            log.info("nacos client: {}", MAPPER.writeValueAsString(service));
            String uri = String.format(uriFormat, service.getHost(), service.getPort());
            UsernameAndPassword requestBody = MAPPER.readValue(parseBodyFromRequest(request), UsernameAndPassword.class);
            log.info("request url: {}, request body: {}", uri, MAPPER.writeValueAsString(requestBody));
            // 向注册中心发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            JwtToken jwtToken = restTemplate.postForObject(
                    uri,
                    new HttpEntity<>(MAPPER.writeValueAsString(requestBody), headers),
                    JwtToken.class
            );
            if (jwtToken != null) {
                return jwtToken.getToken();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 该过滤器的优先级要比 缓存body 那个过滤器低
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }
}
