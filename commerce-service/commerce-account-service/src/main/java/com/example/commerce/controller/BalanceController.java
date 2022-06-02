package com.example.commerce.controller;

import com.example.commerce.account.BalanceInfo;
import com.example.commerce.service.IBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/balance")
@Api(tags = "用户余额服务")
public class BalanceController {

    private final IBalanceService balanceService;

    public BalanceController(IBalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @ApiOperation(value = "查询用户余额", notes = "查询当前用户余额", httpMethod = "GET")
    @GetMapping("current-balance")
    public BalanceInfo getCurrentUserBalanceInfo() {
        return balanceService.getCurrentUserBalanceInfo();
    }

    @ApiOperation(value = "扣减", notes = "扣减用户余额", httpMethod = "PUT")
    @PutMapping("deduct-balance")
    public BalanceInfo deductBalance(@RequestBody BalanceInfo balanceInfo) {
        return balanceService.deductBalanceInfo(balanceInfo);
    }
}
