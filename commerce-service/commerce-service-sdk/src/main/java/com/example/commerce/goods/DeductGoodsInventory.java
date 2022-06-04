package com.example.commerce.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("扣减商品库存对象")
public class DeductGoodsInventory {

    @ApiModelProperty("商品主键 id")
    private Long goodsId;

    @ApiModelProperty("扣减数量")
    private Long count;
}



