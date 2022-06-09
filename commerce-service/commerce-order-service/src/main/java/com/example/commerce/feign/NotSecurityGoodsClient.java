package com.example.commerce.feign;

import com.example.commerce.common.TableId;
import com.example.commerce.goods.DeductGoodsInventory;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 不安全的商品服务接口
 */
@FeignClient(contextId = "notSecurityGoodsClient", value = "commerce-goods-service")
public interface NotSecurityGoodsClient {

    /**
     * 扣减商品库存
     */
    @RequestMapping(value = "/commerce-goods-service/goods/deduct-goods-inventory", method = RequestMethod.PUT)
    CommonResponse<Boolean> deductGoodsInventory(@RequestBody List<DeductGoodsInventory> deductGoodsInventories);

    /**
     * 查询简单商品信息
     */
    @RequestMapping(value = "/commerce-goods-service/goods/simple-goods-info", method = RequestMethod.POST)
    CommonResponse<List<SimpleGoodsInfo>> getSimpleGoodsInfoByTableId(@RequestBody TableId tableId);
}
