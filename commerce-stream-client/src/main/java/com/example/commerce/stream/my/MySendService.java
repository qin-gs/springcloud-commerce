package com.example.commerce.stream.my;

import com.example.commerce.vo.MessageObj;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 使用自定义信道发送消息
 */
@Slf4j
@EnableBinding(MySource.class)
public class MySendService {

    private final MySource mySource;
    private final ObjectMapper mapper;

    public MySendService(MySource mySource, ObjectMapper mapper) {
        this.mySource = mySource;
        this.mapper = mapper;
    }

    /**
     * 使用自定义输出信道发送消息
     */
    public void send(MessageObj message) throws JsonProcessingException {
        log.info("发送消息：{}", message);
        mySource.myOutput().send(
                MessageBuilder.withPayload(mapper.writeValueAsString(message)).build()
        );
    }
}
