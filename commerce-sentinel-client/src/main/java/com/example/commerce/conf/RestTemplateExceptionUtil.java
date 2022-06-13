package com.example.commerce.conf;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.commerce.vo.JwtToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

/**
 * RestTemplate 限流 或 异常 时的处理方法 (需要是 static)
 */
@Slf4j
public class RestTemplateExceptionUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 限流后的处理方法 (若本次访问被限流或服务降级，则调用blockHandler指定的接口)
     */
    public static SentinelClientHttpResponse blockHandler(HttpRequest request,
                                                          byte[] body,
                                                          ClientHttpRequestExecution execution,
                                                          BlockException ex) throws JsonProcessingException {
        log.error("限流了，请稍后再试 url: {}, ex: {}", request.getURI().getPath(), ex.getClass().getCanonicalName());
        return new SentinelClientHttpResponse(MAPPER.writeValueAsString(new JwtToken("限流了，请稍后再试")));
    }

    /**
     * 异常降级之后的处理方法 (若本接口出现未知异常，则调用fallback指定的接口)
     */
    public static SentinelClientHttpResponse fallback(HttpRequest request,
                                                      byte[] body,
                                                      ClientHttpRequestExecution execution,
                                                      BlockException ex) throws JsonProcessingException {
        log.error("fallback，请稍后再试 url: {}, ex: {}", request.getURI().getPath(), ex.getClass().getCanonicalName());
        return new SentinelClientHttpResponse(MAPPER.writeValueAsString(new JwtToken("fallback，请稍后再试")));
    }
}
