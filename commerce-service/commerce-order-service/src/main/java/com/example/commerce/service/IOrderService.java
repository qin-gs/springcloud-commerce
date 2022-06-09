package com.example.commerce.service;

import com.example.commerce.common.TableId;
import com.example.commerce.order.OrderInfo;
import com.example.commerce.vo.PageSimpleOrderDetail;

/**
 * 订单相关服务接口定义
 */
public interface IOrderService {

    /**
     * 创建订单 (创建订单 + 扣减库存 + 扣减余额 + 创建物流)
     */
    TableId createOrder(OrderInfo orderInfo);

    /**
     * 获取当前用户订单信息
     */
    PageSimpleOrderDetail getSimpleOrderDetail(int page);
}
