package com.example.commerce.constant;

public class GatewayConstant {
    /**
     * 登录请求地址
     */
    public static final String LOGIN_URI = "/commerce/login";
    /**
     * 注册请求地址
     */
    public static final String REGISTER_URI = "/commerce/register";
    /**
     * 去授权中心拿到登录 token 的 uri 接口
     */
    public static final String AUTHORITY_CENTER_TOKEN_URI_FORMAT =
            "http://%s:%s/commerce-authority-center/authority/token";
    /**
     * 去授权中心注册并拿到 token 的 uri 接口
     */
    public static final String REGISTER_URI_FORMAT =
            "http://%s:%s/commerce-authority-center/authority/register";
}
