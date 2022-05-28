package com.example.commerce.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;

/**
 * 加上账号认证后，其他微服务需要能注册上去
 */
@Configurable
@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 上下文路径
     */
    private final String contextPath;

    public SecurityConfig(AdminServerProperties properties) {
        this.contextPath = properties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(contextPath + "/");

        http.authorizeRequests()
                // 静态资源放过
                .antMatchers(contextPath + "/assets/**").permitAll()
                .antMatchers(contextPath + "/login").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
                .and()
                // 配置登录登出路径
                .formLogin().loginPage(contextPath + "/login").successHandler(successHandler)
                .and()
                .logout().logoutUrl(contextPath + "/logout")
                .and()
                // 开启 http basic 支持，其他模块注册时需要
                .httpBasic()
                .and()
                // 开启基于 cookie 的 csrf 保护
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // 忽略一些路径的 csrf 保护，以便其他模块可以实现注册
                .ignoringAntMatchers(contextPath + "/instances/**", contextPath + "/actuator/**");
    }
}
