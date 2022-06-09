package com.example.commerce.feign;

import com.example.commerce.common.TableId;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.feign.hystrix.GoodsClientHystrix;
import com.example.commerce.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 提供 fallback 的商品服务接口
 */
@FeignClient(contextId = "securityGoodsClient",
        value = "commerce-goods-service",
        fallback = GoodsClientHystrix.class)
public interface SecurityGoodsClient {

    /**
     * 查询简单商品信息
     */
    @RequestMapping(value = "/commerce-goods-service/goods/simple-goods-info", method = RequestMethod.POST)
    CommonResponse<List<SimpleGoodsInfo>> getSimpleGoodsInfoByTableId(@RequestBody TableId tableId);

}
