package com.example.commerce.dao;

import com.example.commerce.constant.BrandCategory;
import com.example.commerce.constant.GoodsCategory;
import com.example.commerce.entity.CommerceGoods;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CommerceGoodsDao extends PagingAndSortingRepository<CommerceGoods, Long> {

    /**
     * 根据查询条件获取商品信息，限制返回条数
     */
    Optional<CommerceGoods> findFirstByGoodsCategoryAndBrandCategoryAndGoodsName(GoodsCategory goodsCategory, BrandCategory brandCategory, String goodsName);
}
