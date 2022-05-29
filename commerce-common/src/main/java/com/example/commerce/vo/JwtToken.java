package com.example.commerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权中心鉴权之后给客户端的 token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {
    /**
     * jwt 的信息
     */
    private String token;

}
