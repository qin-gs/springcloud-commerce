package com.example.commerce.service;

import com.example.commerce.vo.UsernameAndPassword;

/**
 * jwt 相关服务接口
 */
public interface JwtService {
    /**
     * 生成 jwt token，使用默认的过期时间
     */
    String generateToken(String userName, String password) throws Exception;

    /**
     * 生成 jwt token，指定过期时间
     *
     * @param expire 过期时间，单位：天
     */
    String generateToken(String userName, String password, long expire) throws Exception;

    /**
     * 注册用户并生成 token 返回
     */
    String registerUserAndGetToken(UsernameAndPassword userInfo) throws Exception;

}
