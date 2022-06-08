package com.example.commerce.kafka;

import com.example.commerce.vo.Message;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * kafka 消费者
 */
@Slf4j
@Component
public class KafkaConsumer {

    private final ObjectMapper mapper;

    public KafkaConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 监听 kafka 消息并消费
     */
    @KafkaListener(topics = "kafka-springboot-topic", groupId = "kafka-springboot-group")
    public void listener01(ConsumerRecord<String, String> record) throws JsonProcessingException {

        String key = record.key();
        String value = record.value();
        Message message = mapper.readValue(value, Message.class);
        log.info("kafka 消费消息: key={}, value={}, message={}", key, message, message);
    }

    /**
     * 监听 kafka 消息并消费，如果不确定类型
     */
    @KafkaListener(topics = "kafka-springboot-topic", groupId = "kafka-springboot-group2")
    public void listener02(ConsumerRecord<?, ?> record) throws IOException {

        Optional<?> message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            Message kafkaMessage = mapper.readValue(msg.toString(), Message.class);
            log.info("kafka 消费消息2: message={}", kafkaMessage);
        }
    }

}
