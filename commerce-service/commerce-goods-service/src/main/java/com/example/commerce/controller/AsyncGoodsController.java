package com.example.commerce.controller;

import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.service.async.AsyncTaskManager;
import com.example.commerce.vo.AsyncTaskInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/async-goods")
@Api(tags = "商品异步入库服务")
public class AsyncGoodsController {

    private final AsyncTaskManager taskManager;

    public AsyncGoodsController(AsyncTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @ApiOperation(value = "导入商品", notes = "导入商品到商品表", httpMethod = "POST")
    @PostMapping("import-goods")
    public AsyncTaskInfo importGoods(@RequestBody List<GoodsInfo> goodsInfos) {
        return taskManager.submit(goodsInfos);
    }

    @ApiOperation(value = "查询状态", notes = "查询异步任务执行状态", httpMethod = "GET")
    @GetMapping("task-info")
    public AsyncTaskInfo getTaskInfo(@RequestParam String taskId) {
        return taskManager.getTaskInfo(taskId);
    }
}
