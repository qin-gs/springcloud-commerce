package com.example.commerce.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

/**
 * kafka 消息生产者
 */
@Slf4j
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送消息
     */
    public void sendMessage(String key, String value, String topic) {
        log.info("kafka 发送消息: key={}, value={}, topic={}", key, value, topic);
        if (StringUtils.isAllBlank(value, topic)) {
            throw new IllegalArgumentException("value 和 topic 不能为空");
        }
        // 异步发送消息，判断有没有 key
        ListenableFuture<SendResult<String, String>> future = StringUtils.isEmpty(key) ?
                kafkaTemplate.send(value, topic) : kafkaTemplate.send(key, value, topic);

        // 异步回调获取结果
        future.addCallback(
                result -> {
                    // 获取 topic, 消息发送到的分区, 消息在分区中的偏移量
                    String tp = result.getRecordMetadata().topic();
                    int partition = result.getRecordMetadata().partition();
                    long offset = result.getRecordMetadata().offset();
                    log.info("kafka 发送消息成功: key={}, value={}, topic={}, topic={}, partition={}, offset={}", key, value, topic, tp, partition, offset);
                },
                throwable -> log.error("kafka 发送消息失败: key={}, value={}, topic={}", key, value, topic, throwable)
        );

        // 同步获取结果
        // try {
        //     // 获取节点
        //     SendResult<String, String> result = future.get(5, TimeUnit.SECONDS);
        //     log.info("kafka 发送消息成功: key={}, value={}, topic={}, topic={}, partition={}, offset={}", key, value, topic, result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        // } catch (Exception e) {
        //     log.error("kafka 发送消息失败: key={}, value={}, topic={}", key, value, topic, e);
        // }
    }
}
