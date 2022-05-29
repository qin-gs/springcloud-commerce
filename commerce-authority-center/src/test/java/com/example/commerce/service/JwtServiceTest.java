package com.example.commerce.service;

import com.example.commerce.util.TokenParseUtil;
import com.example.commerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

    /**
     * 根据用户名密码生成 token，解析 token，获取用户信息
     */
    @Test
    public void testGenerateToken() throws Exception {
        String token = jwtService.generateToken("qqq", "5aa765d61d8327de");
        log.info("token: {}", token);
        LoginUserInfo userInfo = TokenParseUtil.parseUserInfoFromToken(token);
        log.info("userInfo: {}", userInfo);
    }

}