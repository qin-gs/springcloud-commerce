package com.example.commerce.filter;

import com.example.commerce.vo.LoginUserInfo;

/**
 * 单独存储每个线程携带的 LoginUserInfo；
 * 需要及时清理 ThreadLocal
 */
public class AccessContext {
    private static final ThreadLocal<LoginUserInfo> loginUserInfos = new ThreadLocal<>();

    public static LoginUserInfo getLoginUserInfo() {
        return loginUserInfos.get();
    }

    public static void setLoginUserInfo(LoginUserInfo loginUserInfo) {
        loginUserInfos.set(loginUserInfo);
    }

    public static void clearLoginUserInfo() {
        loginUserInfos.remove();
    }
}
