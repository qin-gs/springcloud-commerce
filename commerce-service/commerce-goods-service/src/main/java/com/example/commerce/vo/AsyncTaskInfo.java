package com.example.commerce.vo;

import com.example.commerce.constant.AsyncTaskStatusEnum;
import lombok.*;

import java.util.Date;

/**
 * 异步任务执行信息
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskInfo {

    /**
     * 异步任务 id
     */
    private String taskId;

    /**
     * 任务执行状态
     */
    private AsyncTaskStatusEnum status;

    /**
     * 异步任务开始时间
     */
    private Date startTime;

    /**
     * 异步任务结束时间
     */
    private Date endTime;

    /**
     * 总耗时
     */
    private String totalTime;
}
