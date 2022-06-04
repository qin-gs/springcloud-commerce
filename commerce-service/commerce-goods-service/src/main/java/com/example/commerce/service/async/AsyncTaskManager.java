package com.example.commerce.service.async;

import com.example.commerce.constant.AsyncTaskStatusEnum;
import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 异步任务执行管理器
 */
@Slf4j
@Component
public class AsyncTaskManager {

    private final Map<String, AsyncTaskInfo> taskContainer = new HashMap<>(16);

    private final IAsyncService asyncService;

    public AsyncTaskManager(IAsyncService asyncService) {
        this.asyncService = asyncService;
    }

    /**
     * 初始化异步任务
     */
    public AsyncTaskInfo initTask() {
        AsyncTaskInfo taskInfo = AsyncTaskInfo.builder()
                .taskId(UUID.randomUUID().toString())
                .status(AsyncTaskStatusEnum.STARTED)
                .startTime(new Date())
                .build();
        taskContainer.put(taskInfo.getTaskId(), taskInfo);
        return taskInfo;
    }

    /**
     * 提交异步任务
     */
    public AsyncTaskInfo submit(List<GoodsInfo> goodsInfos) {
        AsyncTaskInfo taskInfo = initTask();
        asyncService.asyncImportGoods(goodsInfos, taskInfo.getTaskId());
        return taskInfo;
    }

    /**
     * 设置任务执行状态信息
     */
    public void setTaskInfo(AsyncTaskInfo taskInfo) {
        taskContainer.put(taskInfo.getTaskId(), taskInfo);
    }

    /**
     * 获取任务执行状态信息
     */
    public AsyncTaskInfo getTaskInfo(String taskId) {
        return taskContainer.get(taskId);
    }
}
