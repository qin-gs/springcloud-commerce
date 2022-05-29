package com.example.commerce.dao;


import cn.hutool.crypto.digest.MD5;
import com.example.commerce.AuthorityApplication;
import com.example.commerce.entity.CommerceUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = AuthorityApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CommerceUserDaoTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    @Autowired
    private CommerceUserDao dao;

    @Test
    public void createUser() throws JsonProcessingException {
        CommerceUser user = CommerceUser.builder()
                .username("qqq")
                .password(MD5.create().digestHex16("password"))
                .extraInfo("{}")
                .build();
        log.info("user = [{}]", MAPPER.writeValueAsString(dao.save(user)));
    }

    @Test
    public void findByUsername() throws JsonProcessingException {
        CommerceUser user = dao.findByUsername("qqq");
        log.info("user = [{}]", MAPPER.writeValueAsString(user));
    }

    @Test
    public void findByUsernameAndPassword() throws JsonProcessingException {
        CommerceUser user = dao.findByUsernameAndPassword("qqq", MD5.create().digestHex16("password"));
        log.info("user = [{}]", MAPPER.writeValueAsString(user));
    }
}