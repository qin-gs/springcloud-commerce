package com.example.commerce.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 创建订单时发送的物流消息
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("stream 物流消息对象")
public class LogisticsMessage {

    @ApiModelProperty("用户表主键 id")
    private Long userId;

    @ApiModelProperty("订单表主键 id")
    private Long orderId;

    @ApiModelProperty("地址表主键 id")
    private Long addressId;

    @ApiModelProperty("备注信息(json)")
    private String extraInfo;

}
