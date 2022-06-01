package com.example.commerce.dao;

import com.example.commerce.entity.CommerceBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommerceBalanceDao extends JpaRepository<CommerceBalance, Long> {

    /**
     * 根据 用户id 查询用户余额信息
     */
    CommerceBalance findByUserId(Long userId);
}
