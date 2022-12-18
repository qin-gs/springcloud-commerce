package com.example.commerce.service.communication;

import com.example.commerce.service.hystrix.AuthorityFeignClientFallbackFactory;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 与 authority 通信的接口；
 * 使用后备策略
 */
@FeignClient(value = "commerce-authority-center",
        contextId = "AuthorityFeignClient",
        // fallback = AuthorityFeignClientImpl.class,
        fallbackFactory = AuthorityFeignClientFallbackFactory.class)
public interface AuthorityFeignClient {

    /**
     * 通过 openFeign 获取 token
     */
    @RequestMapping(value = "/commerce-authority-center/authority/token",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    JwtToken getTokenByFeign(@RequestBody UsernameAndPassword usernameAndPassword);
}
