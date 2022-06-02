package com.example.commerce.service.impl;

import com.example.commerce.account.BalanceInfo;
import com.example.commerce.dao.CommerceBalanceDao;
import com.example.commerce.entity.CommerceBalance;
import com.example.commerce.filter.AccessContext;
import com.example.commerce.service.IBalanceService;
import com.example.commerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 余额相关接口
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BalanceServiceImpl implements IBalanceService {

    private final CommerceBalanceDao balanceDao;

    public BalanceServiceImpl(CommerceBalanceDao dao) {
        this.balanceDao = dao;
    }

    @Override
    public BalanceInfo getCurrentUserBalanceInfo() {
        LoginUserInfo userInfo = AccessContext.getLoginUserInfo();

        BalanceInfo balanceInfo = new BalanceInfo(userInfo.getId(), 0L);
        CommerceBalance commerceBalance = balanceDao.findByUserId(userInfo.getId());

        if (commerceBalance != null) {
            balanceInfo.setUserId(commerceBalance.getUserId());
        } else {
            // 还没有用户余额记录，则创建一条
            CommerceBalance newBalance = CommerceBalance.builder()
                    .userId(userInfo.getId())
                    .balance(0L)
                    .build();
            CommerceBalance balance = balanceDao.save(newBalance);
            log.info("创建用户余额记录：{}", balance.getId());
        }
        return balanceInfo;
    }

    @Override
    public BalanceInfo deductBalanceInfo(BalanceInfo balanceInfo) {
        LoginUserInfo userInfo = AccessContext.getLoginUserInfo();
        // 检查用户余额是否足够
        CommerceBalance commerceBalance = balanceDao.findByUserId(userInfo.getId());
        if (commerceBalance == null
                || commerceBalance.getBalance() < balanceInfo.getBalance()) {
            throw new RuntimeException("余额不足");
        }

        if (!balanceInfo.getUserId().equals(userInfo.getId())) {
            log.error("用户信息不匹配，userId={}, userInfo={}", balanceInfo.getUserId(), userInfo);
            throw new RuntimeException("用户信息不匹配");
        }

        commerceBalance.setBalance(commerceBalance.getBalance() - balanceInfo.getBalance());
        CommerceBalance saved = balanceDao.save(commerceBalance);
        log.info("扣除用户余额：{}, {}, {}", userInfo.getId(), balanceInfo.getBalance(), saved.getBalance());

        return BalanceInfo.builder()
                .userId(commerceBalance.getUserId())
                .balance(commerceBalance.getBalance())
                .build();
    }
}
