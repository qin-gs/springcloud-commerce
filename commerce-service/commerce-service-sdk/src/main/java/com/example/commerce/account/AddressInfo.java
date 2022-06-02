package com.example.commerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * 用户地址信息
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "用户地址信息")
public class AddressInfo {

    @ApiModelProperty(value = "地址所属用户 id")
    private Long userId;

    @ApiModelProperty("地址详细信息")
    private List<AddressItem> addressItems;

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(description = "用户单个地址信息")
    public static class AddressItem {

        @ApiModelProperty(value = "地址表主键 id")
        private Long id;

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

        @ApiModelProperty("创建时间")
        private Date createTime;

        @ApiModelProperty("更新时间")
        private Date updateTime;

        public AddressItem(Long id) {
            this.id = id;
        }

        /**
         * 转换  AddressItem  -->  UserAddress
         */
        public UserAddress toUserAddress() {
            return UserAddress.builder()
                    .username(username)
                    .phone(phone)
                    .province(province)
                    .city(city)
                    .addressDetail(addressDetail)
                    .build();
        }
    }
}
