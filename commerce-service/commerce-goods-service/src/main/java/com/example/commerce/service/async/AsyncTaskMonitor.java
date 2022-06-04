package com.example.commerce.service.async;

import com.example.commerce.constant.AsyncTaskStatusEnum;
import com.example.commerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Aspect
@Component
public class AsyncTaskMonitor {

    private final AsyncTaskManager taskManager;

    public AsyncTaskMonitor(AsyncTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * 环绕通知
     */
    @Around("execution(* com.example.commerce.service.async.IAsyncService.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        String taskId = (String) joinPoint.getArgs()[1];
        log.info("当前任务：{}", taskId);
        // 获取任务信息
        AsyncTaskInfo taskInfo = taskManager.getTaskInfo(taskId);
        taskInfo.setStatus(AsyncTaskStatusEnum.RUNNING);

        Object result;
        AsyncTaskStatusEnum status;
        try {
            result = joinPoint.proceed();
            status = AsyncTaskStatusEnum.SUCCESS;
        } catch (Throwable e) {
            result = null;
            status = AsyncTaskStatusEnum.FAILED;
            log.error("异步任务执行失败 taskId: {}, 错误信息: {}", taskId, e.getMessage(), e);
        }
        taskInfo.setEndTime(new Date());
        taskInfo.setStatus(status);
        taskInfo.setTotalTime(String.valueOf(taskInfo.getEndTime().getTime() - taskInfo.getStartTime().getTime()));
        taskManager.setTaskInfo(taskInfo);

        return result;
    }
}
