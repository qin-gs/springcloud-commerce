package com.example.commerce.service.async;

import com.example.commerce.goods.GoodsInfo;

import java.util.List;

/**
 * 异步服务接口定义
 */
public interface IAsyncService {

    /**
     * 异步导入商品信息
     */
    void asyncImportGoods(List<GoodsInfo> goodsInfos, String taskId);
}
