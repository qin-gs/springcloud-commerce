package com.example.commerce.filter;

import com.example.commerce.constant.CommonConstant;
import com.example.commerce.util.TokenParseUtil;
import com.example.commerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户身份统一登录拦截
 * 1. 请求进入 service 之前解析用户信息
 * 2. 请求结束之后清理用户信息
 */
@Slf4j
@Component
public class LoginUserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // swagger2 的一些请求不拦截
        if (checkUrl(request.getRequestURI())) {
            return true;
        }

        // 从 header 中拿到 token
        String token = request.getHeader(CommonConstant.JWT_USER_INFO_KEY);

        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
        } catch (Exception e) {
            log.error("解析用户信息失败，token={}", token, e);
        }

        // 网关层面已经解析过滤，这里不会进去
        if (loginUserInfo == null) {
            throw new RuntimeException("无法解析用户信息");
        }

        log.info("解析用户信息成功：url={}, userId={}, userName={}", request.getRequestURI(), loginUserInfo.getId(), loginUserInfo.getUsername());

        // 在线程中填充用户信息
        AccessContext.setLoginUserInfo(loginUserInfo);

        return true;
    }

    private boolean checkUrl(String url) {
        return StringUtils.containsAny(url,
                "springfox", "swagger", "v2", "webjars", "doc.html");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // 清理用户信息
        if (AccessContext.getLoginUserInfo() != null) {
            AccessContext.clearLoginUserInfo();
        }
    }
}
