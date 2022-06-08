package com.example.commerce.stream;

import com.example.commerce.vo.MessageObj;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * 使用默认通信信道接收消息
 */
@Slf4j
@EnableBinding(Sink.class)
public class DefaultReceiveService {

    private final ObjectMapper mapper;

    public DefaultReceiveService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 使用默认输入信道接收消息
     */
    @StreamListener(Sink.INPUT)
    public void receiveMessage(Object payload) throws JsonProcessingException {
        MessageObj message = mapper.readValue(payload.toString(), MessageObj.class);
        log.info("接收消息 {}", message);
    }
}
