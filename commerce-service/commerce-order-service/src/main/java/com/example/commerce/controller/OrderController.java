package com.example.commerce.controller;

import com.example.commerce.common.TableId;
import com.example.commerce.order.OrderInfo;
import com.example.commerce.service.IOrderService;
import com.example.commerce.vo.PageSimpleOrderDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务对外接口
 */
@Api(tags = "订单服务对外接口")
@Slf4j
@RequestMapping("/order")
@RestController
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @ApiOperation(value = "创建订单", notes = "创建订单(分布式事务): 创建订单 + 扣减库存 + 扣减余额 + 创建物流", httpMethod = "POST")
    @PostMapping("/create-order")
    public TableId createOrder(@RequestBody OrderInfo orderInfo) {
        return orderService.createOrder(orderInfo);
    }

    /**
     * 获取当前用户订单信息
     */
    @ApiOperation(value = "获取订单信息", notes = "获取当前用户订单信息(带有分页)", httpMethod = "GET")
    @GetMapping("/get-simple-order-detail")
    public PageSimpleOrderDetail getSimpleOrderDetail(@RequestParam(required = false, defaultValue = "1") int page) {
        return orderService.getSimpleOrderDetail(page);
    }
}
