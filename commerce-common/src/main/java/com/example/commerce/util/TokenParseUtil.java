package com.example.commerce.util;

import cn.hutool.core.codec.Base64Decoder;
import com.example.commerce.constant.CommonConstant;
import com.example.commerce.vo.LoginUserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * 从 jwt token 中解析数据
 */
public class TokenParseUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64Decoder.decode(CommonConstant.PUBLIC_KEY));
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public static LoginUserInfo parseUserInfoFromToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        if (token == null) {
            return null;
        }
        Jws<Claims> claimsJws = parseToken(token, getPublicKey());

        // 如果 token 过期了
        Claims body = claimsJws.getBody();
        if (body.getExpiration().before(new Date())) {
            return null;
        }

        // 返回 token 中的信息
        return MAPPER.readValue(body.get(CommonConstant.JWT_USER_INFO_KEY).toString(), LoginUserInfo.class);
    }

    public static Jws<Claims> parseToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }
}
