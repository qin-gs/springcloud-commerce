package com.example.commerce.service.impl;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.common.TableId;
import com.example.commerce.dao.CommerceOrderDao;
import com.example.commerce.feign.NotSecurityBalanceClient;
import com.example.commerce.feign.NotSecurityGoodsClient;
import com.example.commerce.feign.SecurityAddressClient;
import com.example.commerce.feign.SecurityGoodsClient;
import com.example.commerce.order.OrderInfo;
import com.example.commerce.service.IOrderService;
import com.example.commerce.source.LogisticsSource;
import com.example.commerce.vo.CommonResponse;
import com.example.commerce.vo.PageSimpleOrderDetail;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 订单相关服务接口实现
 */
@Slf4j
@Service
@EnableBinding(LogisticsSource.class)
public class OrderServiceImpl implements IOrderService {

    private CommerceOrderDao commerceOrderDao;
    /**
     * feign 客户端
     */
    private SecurityAddressClient securityAddressClient;
    private SecurityGoodsClient securityGoodsClient;
    private NotSecurityGoodsClient notSecurityGoodsClient;
    private NotSecurityBalanceClient notSecurityBalanceClient;
    /**
     * stream 发射器
     */
    private LogisticsSource logisticsSource;

    public OrderServiceImpl(CommerceOrderDao commerceOrderDao,
                            SecurityAddressClient securityAddressClient,
                            SecurityGoodsClient securityGoodsClient,
                            NotSecurityGoodsClient notSecurityGoodsClient,
                            NotSecurityBalanceClient notSecurityBalanceClient,
                            LogisticsSource logisticsSource) {
        this.commerceOrderDao = commerceOrderDao;
        this.securityAddressClient = securityAddressClient;
        this.securityGoodsClient = securityGoodsClient;
        this.notSecurityGoodsClient = notSecurityGoodsClient;
        this.notSecurityBalanceClient = notSecurityBalanceClient;
        this.logisticsSource = logisticsSource;
    }

    /**
     * 分布式事务
     * 创建订单：创建订单 + 扣减库存 + 扣减余额 + 创建物流消息；
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public TableId createOrder(OrderInfo orderInfo) {
        // 获取地址信息
        CommonResponse<AddressInfo> addressInfo = securityAddressClient.getAddressInfoByTableId(
                new TableId(Collections.singletonList(new TableId.Id(orderInfo.getUserAddress()))));
        // 校验请求对象是否合法


        return null;
    }

    @Override
    public PageSimpleOrderDetail getSimpleOrderDetail(int page) {
        return null;
    }
}
