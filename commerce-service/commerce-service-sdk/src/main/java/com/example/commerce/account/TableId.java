package com.example.commerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * 主键
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("通用 id 对象")
public class TableId {

    @ApiModelProperty("数据表记录主键")
    private List<Id> ids;

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel("数据表记录主键对象")
    public static class Id {

        @ApiModelProperty("数据表记录主键")
        private Long id;
    }
}
