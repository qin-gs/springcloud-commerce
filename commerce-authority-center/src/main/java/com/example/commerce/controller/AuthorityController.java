package com.example.commerce.controller;

import com.example.commerce.annotation.IgnoreResponseAdvice;
import com.example.commerce.service.JwtService;
import com.example.commerce.vo.JwtToken;
import com.example.commerce.vo.UsernameAndPassword;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("authority")
public class AuthorityController {

    private final ObjectMapper MAPPER = new ObjectMapper();

    private final JwtService jwtService;

    public AuthorityController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * 登录：从授权中心获取 token
     */
    @IgnoreResponseAdvice
    @PostMapping("token")
    public JwtToken token(@RequestBody UsernameAndPassword userInfo) throws Exception {
        log.info("request to get token with params: {}", MAPPER.writeValueAsString(userInfo));
        return new JwtToken(jwtService.generateToken(userInfo.getUsername(), userInfo.getPassword()));
    }

    /**
     * 注册：创建用户并返回 token
     */
    @IgnoreResponseAdvice
    @PostMapping("register")
    public JwtToken register(@RequestBody UsernameAndPassword userInfo) throws Exception {
        log.info("register user: {}", MAPPER.writeValueAsString(userInfo));
        return new JwtToken(jwtService.registerUserAndGetToken(userInfo));
    }
}
