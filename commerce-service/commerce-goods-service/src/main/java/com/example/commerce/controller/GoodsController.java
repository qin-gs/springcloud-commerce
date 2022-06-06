package com.example.commerce.controller;

import com.example.commerce.common.TableId;
import com.example.commerce.goods.DeductGoodsInventory;
import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.service.IGoodsService;
import com.example.commerce.vo.PageSimpleGoodsInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品微服务对外接口
 */
@Slf4j
@RestController
@RequestMapping("/goods")
@Api(tags = "商品微服务接口")
public class GoodsController {

    private final IGoodsService service;

    public GoodsController(IGoodsService service) {
        this.service = service;
    }

    @ApiOperation(value = "详细商品信息", notes = "根据 TableId 获取商品详细信息", httpMethod = "POST")
    @PostMapping("goods-info")
    public List<GoodsInfo> getGoodsInfoByTableId(@RequestBody TableId tableId) {
        return service.getGoodsInfoByTableId(tableId);
    }

    @ApiOperation(value = "简单商品信息", notes = "获取分页的简单商品信息", httpMethod = "GET")
    @GetMapping("page-simple-goods-info")
    public PageSimpleGoodsInfo getSimpleGoodsInfoByPage(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return service.getSimpleGoodsInfoByPage(page);
    }

    @ApiOperation(value = "简单商品信息", notes = "根据 TableId 查询简单商品信息", httpMethod = "POST")
    @PostMapping("simple-goods-info")
    public List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(@RequestBody TableId tableId) {
        return service.getSimpleGoodsInfoByTableId(tableId);
    }

    @ApiOperation(value = "扣减商品库存", notes = "扣减商品库存", httpMethod = "PUT")
    @PutMapping("deduct-goods-inventory")
    public boolean deductGoodsInventory(@RequestBody List<DeductGoodsInventory> inventories) {
        return service.deductGoodsInventory(inventories);
    }

}
