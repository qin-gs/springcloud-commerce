package com.example.commerce.service;

import com.example.commerce.account.BalanceInfo;

/**
 * 余额相关接口
 */
public interface IBalanceService {

    /**
     * 获取当前用户余额信息
     */
    BalanceInfo getCurrentUserBalanceInfo();

    /**
     * 扣减余额
     *
     * @param balanceInfo 想要扣减的余额信息
     */
    BalanceInfo deductBalanceInfo(BalanceInfo balanceInfo);
}
