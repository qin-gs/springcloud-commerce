package com.example.commerce.service;

import com.example.commerce.common.TableId;
import com.example.commerce.goods.DeductGoodsInventory;
import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.vo.PageSimpleGoodsInfo;

import java.util.List;

/**
 * 商品微服务接口
 */
public interface IGoodsService {

    /**
     * 根据 table id 查询商品信息
     */
    List<GoodsInfo> getGoodsInfoByTableId(TableId tableId);

    /**
     * 获取分页商品信息
     */
    PageSimpleGoodsInfo getSimpleGoodsInfoByPage(int page);

    /**
     * 根据 TableId 查询简单商品信息
     */
    List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(TableId tableId);

    /**
     * 扣减商品库存
     */
    boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories);
}
