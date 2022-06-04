package com.example.commerce.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "详细的商品信息")
public class GoodsInfo {

    @ApiModelProperty(value = "商品表主键 id")
    private Long id;

    @ApiModelProperty(value = "商品类别编码")
    private String goodsCategory;

    @ApiModelProperty("品牌分类编码")
    private String brandCategory;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsPic;

    @ApiModelProperty(value = "描述信息")
    private String goodsDescription;

    @ApiModelProperty(value = "商品状态")
    private Integer goodsStatus;

    @ApiModelProperty(value = "商品价格")
    private Integer price;

    @ApiModelProperty(value = "商品属性")
    private GoodsProperty goodsProperty;

    @ApiModelProperty(value = "供应量")
    private Long supply;

    @ApiModelProperty(value = "商品库存")
    private Long inventory;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(description = "商品属性信息")
    public static class GoodsProperty {

        @ApiModelProperty("尺寸")
        private String size;

        @ApiModelProperty("颜色")
        private String color;

        @ApiModelProperty("材质")
        private String material;

        @ApiModelProperty("图案")
        private String pattern;
    }
}
