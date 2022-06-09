package com.example.commerce.vo;

import com.example.commerce.account.UserAddress;
import com.example.commerce.goods.SimpleGoodsInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "分页订单详情对象")
public class PageSimpleOrderDetail {

    @ApiModelProperty("订单详情")
    private List<SingleOrderItem> orderItems;

    @ApiModelProperty("是否有下一页(更多订单)")
    private boolean hasMore;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "单个订单信息对象")
    public static class SingleOrderItem {

        @ApiModelProperty("订单表主键 id")
        private Long id;

        @ApiModelProperty("用户地址信息")
        private UserAddress userAddress;

        @ApiModelProperty("订单中的商品信息")
        private List<SingleOrderGoodsItem> goodsItems;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "单个订单中的单项商品信息")
    public static class SingleOrderGoodsItem {

        @ApiModelProperty("简单商品信息")
        private SimpleGoodsInfo simpleGoodsInfo;

        @ApiModelProperty("商品个数")
        private Integer count;
    }
}
