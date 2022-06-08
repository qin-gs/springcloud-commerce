package com.example.commerce.partition;

import com.example.commerce.vo.MessageObj;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * key 提取策略
 */
@Slf4j
@Component
public class MyPartitionKeyExtractorName implements PartitionKeyExtractorStrategy {
    private ObjectMapper mapper;

    public MyPartitionKeyExtractorName(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object extractKey(Message<?> message) {
        try {
            MessageObj msg = mapper.readValue(message.getPayload().toString(), MessageObj.class);
            String key = msg.getProjectName();
            log.info("提取的 key: {}", key);
            return key;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
