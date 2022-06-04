package com.example.commerce.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "简单的商品信息")
public class SimpleGoodsInfo {

    @ApiModelProperty(value = "商品表主键 id")
    private Long id;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsPic;

    @ApiModelProperty(value = "商品价格")
    private Integer price;

    public SimpleGoodsInfo(Long id) {
        this.id = id;
    }
}
