package com.example.commerce.controller;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.common.TableId;
import com.example.commerce.service.IAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/address")
@Validated
@Api(tags = "用户地址服务")
public class AddressController {

    private final IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * value 是描述，notes 是详细信息
     */
    @ApiOperation(value = "创建", notes = "添加用户地址", httpMethod = "POST")
    @PostMapping("create-address")
    public TableId createAddressInfo(@RequestBody AddressInfo addressInfo) {
        return addressService.createAddressInfo(addressInfo);
    }

    @ApiOperation(value = "查询", notes = "查询用户地址", httpMethod = "GET")
    @GetMapping("current-address")
    public AddressInfo getCurrentAddressInfo() {
        return addressService.getCurrentAddressInfo();
    }

    @ApiOperation(value = "获取用户地址信息", notes = "通过 id 获取用户地址，id 是 CommerceAddress 表的主键", httpMethod = "GET")
    @GetMapping("address-info")
    public AddressInfo getAddressInfoById(@RequestParam Long id) {
        return addressService.getAddressInfoById(id);
    }

    @ApiOperation(value = "获取用户地址信息", notes = "通过 TableId 获取用户地址", httpMethod = "POST")
    @PostMapping("address-info-by-table-id")
    public AddressInfo getAddressInfoByTableId(@RequestBody TableId tableId) {
        return addressService.getAddressByTableId(tableId);
    }
}
