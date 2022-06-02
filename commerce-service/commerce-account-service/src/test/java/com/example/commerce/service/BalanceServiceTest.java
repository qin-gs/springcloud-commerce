package com.example.commerce.service;

import com.example.commerce.account.BalanceInfo;
import com.example.commerce.filter.AccessContext;
import com.example.commerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BalanceServiceTest extends BaseTest {

    @Autowired
    private IBalanceService service;

    @Test
    public void getCurrentUserBalanceInfo() {
        BalanceInfo info = service.getCurrentUserBalanceInfo();
        log.info("当前用户的余额信息：{}", info);
    }

    @Test
    public void deductBalanceInfo() {

        LoginUserInfo userInfo = AccessContext.getLoginUserInfo();
        BalanceInfo info = service.deductBalanceInfo(BalanceInfo.builder().userId(userInfo.getId()).balance(100L).build());
        log.info("扣减后的余额信息：{}", info);
    }
}
