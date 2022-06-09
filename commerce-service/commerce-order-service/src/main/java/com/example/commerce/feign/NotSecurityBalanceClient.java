package com.example.commerce.feign;

import com.example.commerce.account.BalanceInfo;
import com.example.commerce.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 不安全的服务 (没有 fallback)
 */
@FeignClient(contextId = "notSecurityBalanceClient",
        value = "commerce-account-service")
public interface NotSecurityBalanceClient {

    @RequestMapping(value = "/commerce-account-service/balance/deduct-balance", method = RequestMethod.PUT)
    CommonResponse<BalanceInfo> deductBalance(@RequestBody BalanceInfo balanceInfo);
}
