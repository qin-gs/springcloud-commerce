package com.example.commerce.service;

import com.example.commerce.filter.AccessContext;
import com.example.commerce.vo.LoginUserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试用户的基础类，填充用户信息
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {

    protected final LoginUserInfo userInfo = new LoginUserInfo(10L, "qqq");

    @Before
    public void before() {
        AccessContext.setLoginUserInfo(userInfo);
    }

    @After
    public void after() {
        AccessContext.clearLoginUserInfo();
    }

}
