package com.example.commerce.stream.my;

import com.example.commerce.vo.MessageObj;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * 使用自定义输入信道接收消息
 */
@Slf4j
@EnableBinding(MySink.class)
public class MyReceiveService {

    private MySink mySink;
    private ObjectMapper mapper;

    public MyReceiveService(MySink mySink, ObjectMapper mapper) {
        this.mySink = mySink;
        this.mapper = mapper;
    }

    /**
     * 监听自定义的信道
     */
    @StreamListener(MySink.INPUT)
    public void receiveMessage(@Payload Object payload) throws JsonProcessingException {
        MessageObj message = mapper.readValue(payload.toString(), MessageObj.class);
        log.info("接收消息: {}", message);
    }
}
