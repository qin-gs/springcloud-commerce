package com.example.commerce.feign.hystrix;

import com.example.commerce.common.TableId;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.feign.SecurityGoodsClient;
import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 商品服务熔断降级策略
 */
@Slf4j
@Component
public class GoodsClientHystrix implements SecurityGoodsClient {

    @Override
    public CommonResponse<List<SimpleGoodsInfo>> getSimpleGoodsInfoByTableId(TableId tableId) {
        log.error("获取商品信息失败，tableId={}", tableId);
        return new CommonResponse<>(-1, "获取商品信息失败", Collections.emptyList());
    }
}
