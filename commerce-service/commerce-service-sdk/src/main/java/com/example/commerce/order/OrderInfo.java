package com.example.commerce.order;

import com.example.commerce.goods.DeductGoodsInventory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * 订单信息
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户发起的订单信息")
public class OrderInfo {

    @ApiModelProperty(value = "用户地址信息")
    private Long userAddress;

    @ApiModelProperty(value = "订单中的商品信息")
    private List<OrderItem> orderItems;

    @ApiModel(description = "订单中的商品信息")
    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {

        @ApiModelProperty(value = "商品表主键 id")
        private Long goodsId;

        @ApiModelProperty(value = "购买商品数量")
        private Integer count;

        public DeductGoodsInventory toDeductGoodsInventory() {
            return new DeductGoodsInventory(goodsId, count);
        }

    }
}
