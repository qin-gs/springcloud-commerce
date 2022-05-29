package com.example.commerce.dao;

import com.example.commerce.entity.CommerceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户操作 (数据表，主键类型)
 */
@Repository
public interface CommerceUserDao extends JpaRepository<CommerceUser, Long> {

    /**
     * 根据用户名查询用户
     */
    CommerceUser findByUsername(String username);

    /**
     * 根据用户名密码查询用户
     */
    CommerceUser findByUsernameAndPassword(String username, String password);
}
