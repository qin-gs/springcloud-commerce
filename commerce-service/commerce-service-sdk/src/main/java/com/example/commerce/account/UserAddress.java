package com.example.commerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 用户地址信息
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("用户地址信息")
public class UserAddress {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("详细地址")
    private String addressDetail;

}
