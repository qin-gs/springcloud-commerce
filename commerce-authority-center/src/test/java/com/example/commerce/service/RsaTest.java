package com.example.commerce.service;

import cn.hutool.core.codec.Base64;
import com.example.commerce.AuthorityApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * rsa256 非对称加密算法：生成公钥和私钥
 */
@Slf4j
public class RsaTest {

    @Test
    public void generateKeyBytes() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("rsa");
        generator.initialize(2048);
        // 生成公钥和私钥
        KeyPair pair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();

        log.info("publicKey: [{}]", Base64.encode(publicKey.getEncoded()));
        log.info("privateKey: [{}]", Base64.encode(privateKey.getEncoded()));
    }
}
