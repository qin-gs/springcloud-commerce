package com.example.commerce.service.impl;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.account.BalanceInfo;
import com.example.commerce.common.TableId;
import com.example.commerce.dao.CommerceOrderDao;
import com.example.commerce.entity.CommerceOrder;
import com.example.commerce.feign.NotSecurityBalanceClient;
import com.example.commerce.feign.NotSecurityGoodsClient;
import com.example.commerce.feign.SecurityAddressClient;
import com.example.commerce.feign.SecurityGoodsClient;
import com.example.commerce.filter.AccessContext;
import com.example.commerce.goods.DeductGoodsInventory;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.order.LogisticsMessage;
import com.example.commerce.order.OrderInfo;
import com.example.commerce.service.IOrderService;
import com.example.commerce.source.LogisticsSource;
import com.example.commerce.vo.PageSimpleOrderDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

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
    private ObjectMapper mapper;

    public OrderServiceImpl(CommerceOrderDao commerceOrderDao,
                            SecurityAddressClient securityAddressClient,
                            SecurityGoodsClient securityGoodsClient,
                            NotSecurityGoodsClient notSecurityGoodsClient,
                            NotSecurityBalanceClient notSecurityBalanceClient,
                            LogisticsSource logisticsSource,
                            ObjectMapper mapper) {
        this.commerceOrderDao = commerceOrderDao;
        this.securityAddressClient = securityAddressClient;
        this.securityGoodsClient = securityGoodsClient;
        this.notSecurityGoodsClient = notSecurityGoodsClient;
        this.notSecurityBalanceClient = notSecurityBalanceClient;
        this.logisticsSource = logisticsSource;
        this.mapper = mapper;
    }

    /**
     * 分布式事务
     * 创建订单：创建订单 + 扣减库存 + 扣减余额 + 创建物流消息；
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public TableId createOrder(OrderInfo orderInfo) {
        // 获取地址信息
        AddressInfo addressInfo = securityAddressClient.getAddressInfoByTableId(
                new TableId(Collections.singletonList(new TableId.Id(orderInfo.getUserAddress())))).getData();
        // 校验请求对象是否合法 (商品信息不处理，减库存的时候检查)
        if (CollectionUtils.isEmpty(addressInfo.getAddressItems())) {
            throw new RuntimeException("地址信息不存在: " + orderInfo.getUserAddress());
        }

        // 创建订单
        CommerceOrder commerceOrder = null;
        try {
            commerceOrder = commerceOrderDao.save(
                    new CommerceOrder(
                            AccessContext.getLoginUserInfo().getId(),
                            orderInfo.getUserAddress(),
                            mapper.writeValueAsString(orderInfo.getOrderItems())
                    )
            );
            log.info("创建订单成功 user: {}, order: {}", AccessContext.getLoginUserInfo().getId(), commerceOrder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 扣减库存
        if (!notSecurityGoodsClient.deductGoodsInventory(
                orderInfo.getOrderItems().stream()
                        .map(OrderInfo.OrderItem::toDeductGoodsInventory)
                        .collect(toList())
        ).getData()) {
            throw new RuntimeException("扣减库存失败");
        } else {
            log.info("扣减库存成功 user: {}, order: {}", AccessContext.getLoginUserInfo().getId(), orderInfo);
        }

        // 扣减账户余额 (计算总价，减扣)
        // 获取商品信息，计算总价
        List<SimpleGoodsInfo> goodsInfos = notSecurityGoodsClient.getSimpleGoodsInfoByTableId(
                new TableId(
                        orderInfo.getOrderItems().stream()
                                .map(item -> new TableId.Id(item.getGoodsId()))
                                .collect(toList())
                )
        ).getData();
        Map<Long, SimpleGoodsInfo> goodsId2GoodsInfo = goodsInfos.stream()
                .collect(toMap(SimpleGoodsInfo::getId, Function.identity()));
        Integer balance = orderInfo.getOrderItems().stream()
                .map(item -> goodsId2GoodsInfo.get(item.getGoodsId()).getPrice() * item.getCount())
                .reduce(0, Integer::sum);
        assert balance > 0;

        BalanceInfo balanceInfo = notSecurityBalanceClient.deductBalance(
                new BalanceInfo(AccessContext.getLoginUserInfo().getId(), Long.valueOf(String.valueOf(balance)))
        ).getData();
        if (balanceInfo == null) {
            throw new RuntimeException("扣减余额失败");
        } else {
            log.info("扣减余额成功 user: {}, balanceInfo: {}", AccessContext.getLoginUserInfo().getId(), balanceInfo);
        }

        // 创建物流消息
        try {
            LogisticsMessage logisticsMessage = new LogisticsMessage(
                    AccessContext.getLoginUserInfo().getId(),
                    commerceOrder.getId(),
                    orderInfo.getUserAddress(),
                    null
            );
            if (!logisticsSource.logisticsOutput().send(MessageBuilder.withPayload(mapper.writeValueAsString(logisticsMessage)).build())) {
                log.error("创建物流消息失败 user: {}, 消息: {}", AccessContext.getLoginUserInfo().getId(), logisticsMessage);
                throw new RuntimeException("创建物流消息失败: " + logisticsMessage);
            }
            log.info("创建物流消息成功 user: {}, 消息: {}", AccessContext.getLoginUserInfo().getId(), logisticsMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 返回订单 id
        return new TableId(Collections.singletonList(new TableId.Id(commerceOrder.getId())));
    }

    @Override
    public PageSimpleOrderDetail getSimpleOrderDetail(int page) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<CommerceOrder> orderPage = commerceOrderDao.findAllByUserId(AccessContext.getLoginUserInfo().getId(), pageable);
        List<CommerceOrder> orders = orderPage.getContent();
        if (CollectionUtils.isEmpty(orders)) {
            return new PageSimpleOrderDetail(Collections.emptyList(), false);
        }
        // 获取当前订单中的所有 goodsId (不会为空)
        Set<Long> goodsIdsInOrders = orders.stream().map(order -> {
            try {
                List<DeductGoodsInventory> deductGoodsInventories = mapper.readValue(order.getOrderDetail(), new TypeReference<List<DeductGoodsInventory>>() {
                });
                return deductGoodsInventories.stream().map(DeductGoodsInventory::getGoodsId).collect(toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).flatMap(Collection::stream).collect(toSet());

        // 是否有下一页
        boolean hasNext = orderPage.getTotalPages() > page;

        // 获取商品信息
        List<SimpleGoodsInfo> goodsInfos = securityGoodsClient.getSimpleGoodsInfoByTableId(
                new TableId(goodsIdsInOrders.stream().map(TableId.Id::new).collect(toList()))
        ).getData();
        // 获取地址信息
        AddressInfo addressInfo = securityAddressClient.getAddressInfoByTableId(
                new TableId(orders.stream()
                        .map(order -> new TableId.Id(order.getAddressId()))
                        .distinct().collect(toList()))
        ).getData();
        // 组装商品中的地址信息

        return new PageSimpleOrderDetail(assembleSingleOrderItems(orders, goodsInfos, addressInfo), hasNext);
    }

    /**
     * 组装订单详情
     */
    private List<PageSimpleOrderDetail.SingleOrderItem> assembleSingleOrderItems(List<CommerceOrder> orders,
                                                                                 List<SimpleGoodsInfo> goodsInfos,
                                                                                 AddressInfo addressInfo) {
        Map<Long, SimpleGoodsInfo> id2GoodsInfo = goodsInfos.stream()
                .collect(toMap(SimpleGoodsInfo::getId, Function.identity()));
        Map<Long, AddressInfo.AddressItem> id2AddressItem = addressInfo.getAddressItems().stream()
                .collect(toMap(AddressInfo.AddressItem::getId, Function.identity()));

        return orders.stream()
                .map(order -> PageSimpleOrderDetail.SingleOrderItem.builder()
                        .id(order.getId())
                        .userAddress(id2AddressItem.getOrDefault(order.getAddressId(), new AddressInfo.AddressItem(-1L)).toUserAddress())
                        .goodsItems(buildSingleOrderGoodsItem(order, id2GoodsInfo)).build())
                .collect(toList());
    }

    private List<PageSimpleOrderDetail.SingleOrderGoodsItem> buildSingleOrderGoodsItem(CommerceOrder order,
                                                                                       Map<Long, SimpleGoodsInfo> id2GoodsInfo) {
        try {
            List<DeductGoodsInventory> deductGoodsInventories = mapper.readValue(order.getOrderDetail(), new TypeReference<List<DeductGoodsInventory>>() {
            });
            return deductGoodsInventories.stream()
                    .map(deductGoodsInventory -> PageSimpleOrderDetail.SingleOrderGoodsItem.builder()
                            .count(deductGoodsInventory.getCount())
                            .simpleGoodsInfo(id2GoodsInfo.getOrDefault(deductGoodsInventory.getGoodsId(), new SimpleGoodsInfo(-1L)))
                            .build())
                    .collect(toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
