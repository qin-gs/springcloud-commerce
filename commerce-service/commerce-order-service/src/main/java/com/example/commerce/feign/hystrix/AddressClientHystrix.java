package com.example.commerce.feign.hystrix;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.common.TableId;
import com.example.commerce.feign.SecurityAddressClient;
import com.example.commerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 账户服务熔断降级策略
 */
@Slf4j
@Component
public class AddressClientHystrix implements SecurityAddressClient {

    @Override
    public CommonResponse<AddressInfo> getAddressInfoByTableId(TableId tableId) {
        log.error("获取地址信息失败，tableId={}", tableId);
        return new CommonResponse<>(-1, "获取地址信息失败", new AddressInfo(-1L, Collections.emptyList()));
    }
}
