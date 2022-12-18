package com.example.commerce.service.hystrix;

import com.example.commerce.service.communication.AuthorityFeignClient;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 后备策略；
 * 不能知道发生了什么异常
 */
@Slf4j
@Component
public class AuthorityFeignClientImpl implements AuthorityFeignClient {

    @Override
    public JwtToken getTokenByFeign(UsernameAndPassword usernameAndPassword) {

        log.info("feign 获取 token (后备策略测试): {}", usernameAndPassword);

        return new JwtToken("来自 fallback ");
    }
}
