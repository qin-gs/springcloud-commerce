package com.example.commerce.dao;

import com.example.commerce.entity.CommerceAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommerceAddressDao extends JpaRepository<CommerceAddress, Long> {

    /**
     * 根据用户 id 查询地址信息
     */
    List<CommerceAddress> findAllByUserId(Long id);
}
