package com.example.commerce.service.communication.hystrix;

import com.example.commerce.service.communication.AuthorityFeignClient;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * openFeign 集成 hystrix 另一种模式；
 * 可以知道产生了什么异常
 */
@Slf4j
@Component
public class AuthorityFeignClientFallbackFactory implements FallbackFactory<AuthorityFeignClient> {

    @Override
    public AuthorityFeignClient create(Throwable cause) {
        log.warn("feign 获取 token (FallbackFactory), 出现异常: {}", cause.getMessage(), cause);
        return new AuthorityFeignClient() {
            @Override
            public JwtToken getTokenByFeign(UsernameAndPassword usernameAndPassword) {
                return new JwtToken("来自 fallback factory");
            }
        };
    }
}
