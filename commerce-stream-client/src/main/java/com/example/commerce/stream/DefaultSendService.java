package com.example.commerce.stream;

import com.example.commerce.vo.MessageObj;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 使用默认通信信道发送消息
 */
@Slf4j
@EnableBinding(Source.class)
public class DefaultSendService {

    private Source source;
    private ObjectMapper mapper;

    public DefaultSendService(Source source, ObjectMapper mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    /**
     * 使用默认输出信道发送消息
     */
    public void sendMessage(MessageObj message) throws JsonProcessingException {
        String msg = mapper.writeValueAsString(message);
        log.info("发送消息 {}", msg);

        // 统一消息的编程模型
        source.output().send(MessageBuilder.withPayload(msg).build());
    }

}
