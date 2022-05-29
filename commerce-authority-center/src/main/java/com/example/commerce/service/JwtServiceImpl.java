package com.example.commerce.service;

import cn.hutool.core.codec.Base64Decoder;
import com.example.commerce.constant.AuthorityConstant;
import com.example.commerce.constant.CommonConstant;
import com.example.commerce.dao.CommerceUserDao;
import com.example.commerce.entity.CommerceUser;
import com.example.commerce.vo.LoginUserInfo;
import com.example.commerce.vo.UsernameAndPassword;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JwtServiceImpl implements JwtService {

    private final ObjectMapper MAPPER = new ObjectMapper();

    private final CommerceUserDao commerceUserDao;

    public JwtServiceImpl(CommerceUserDao dao) {
        this.commerceUserDao = dao;
    }

    @Override
    public String generateToken(String userName, String password) throws Exception {
        return generateToken(userName, password, 0);
    }

    @Override
    public String generateToken(String userName, String password, long expire) throws Exception {
        // 验证用户名和密码是否正确
        CommerceUser user = commerceUserDao.findByUsernameAndPassword(userName, password);
        if (user == null) {
            log.error("can't find user [{}] [{}]", userName, password);
            return null;
        }
        LoginUserInfo userInfo = new LoginUserInfo(user.getId(), user.getUsername());
        if (expire <= 0) {
            expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        }
        // 计算超时时间
        ZonedDateTime zdt = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zdt.toInstant());

        return Jwts.builder()
                // payload, id, expireDate, sign(签名)
                .claim(CommonConstant.JWT_USER_INFO_KEY, MAPPER.writeValueAsString(userInfo))
                .setId(UUID.randomUUID().toString())
                .setExpiration(expireDate)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64Decoder.decode(AuthorityConstant.PRIVATE_KEY));
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    @Override
    public String registerUserAndGetToken(UsernameAndPassword userInfo) throws Exception {
        // 校验用户名是否已经存在
        CommerceUser isUser = commerceUserDao.findByUsername(userInfo.getUsername());
        if (isUser != null) {
            log.error("user is registered: [{}]", userInfo.getUsername());
        }
        CommerceUser user = CommerceUser.builder()
                .username(userInfo.getUsername())
                .password(userInfo.getPassword())
                .extraInfo("{}")
                .build();
        user = commerceUserDao.save(user);
        log.info("register success: [{}] [{}]", user.getId(), user.getUsername());
        return generateToken(user.getUsername(), user.getPassword());
    }
}
