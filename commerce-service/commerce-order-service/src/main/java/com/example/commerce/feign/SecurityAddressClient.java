package com.example.commerce.feign;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.common.TableId;
import com.example.commerce.feign.hystrix.AddressClientHystrix;
import com.example.commerce.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 安全的地址服务接口
 */
@FeignClient(contextId = "securityAddressClient",
        value = "commerce-account-service",
        fallback = AddressClientHystrix.class)
public interface SecurityAddressClient {

    @RequestMapping(value = "/commerce-account-service/address/address-info-by-table-id", method = RequestMethod.POST)
    CommonResponse<AddressInfo> getAddressInfoByTableId(@RequestBody TableId tableId);
}
