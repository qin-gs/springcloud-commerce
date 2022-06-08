package com.example.commerce.partition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;
import org.springframework.stereotype.Component;

/**
 * 决定 payload 发送到哪个分区的策略
 */
@Slf4j
@Component
public class MyPartitionSelectorMame implements PartitionSelectorStrategy {

    /**
     * 决定 payload 发送到哪个分区的策略
     */
    @Override
    public int selectPartition(Object key, int partitionCount) {
        int partition = key.toString().hashCode() % partitionCount;
        log.info("选择的分区: {}, key: {}, partitionCount: {}", partition, key, partitionCount);
        return partition;
    }
}
