package com.example.commerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录信息 (从 jwt 的 payload 中解析出来的)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserInfo {
    private Long id;
    private String username;
}
