package com.example.commerce.dao;

import com.example.commerce.entity.CommerceOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommerceOrderDao extends PagingAndSortingRepository<CommerceOrder, Long> {

    /**
     * 根据 userId 查询分页订单
     */
    Page<CommerceOrder> findAllByUserId(Long userId, Pageable pageable);

}
