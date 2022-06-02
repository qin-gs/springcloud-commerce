package com.example.commerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 用户账户余额信息
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户账户余额信息")
public class BalanceInfo {

    @ApiModelProperty("用户主键")
    private Long userId;

    @ApiModelProperty("用户账户余额")
    private Long balance;
}
