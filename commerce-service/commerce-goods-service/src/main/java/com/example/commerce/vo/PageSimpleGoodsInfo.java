package com.example.commerce.vo;

import com.example.commerce.goods.SimpleGoodsInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * 分页商品信息
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "分页商品信息")
public class PageSimpleGoodsInfo {

    @ApiModelProperty("分页简单商品信息")
    private List<SimpleGoodsInfo> simpleGoodsInfos;

    @ApiModelProperty("是否有下一页")
    private boolean hasMore;

}
