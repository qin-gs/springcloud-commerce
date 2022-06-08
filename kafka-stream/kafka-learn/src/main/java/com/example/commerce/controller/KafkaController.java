package com.example.commerce.controller;

import com.example.commerce.kafka.KafkaProducer;
import com.example.commerce.vo.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * springboot 集成 kafka 发送消息
 */
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private ObjectMapper mapper;
    private KafkaProducer kafkaProducer;

    public KafkaController(ObjectMapper mapper, KafkaProducer kafkaProducer) {
        this.mapper = mapper;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * 发送消息
     */
    @GetMapping("/send-message")
    public void sendMessage(@RequestParam(required = false) String key,
                            @RequestParam String topic) throws JsonProcessingException {

        Message message = Message.builder().id(1).projectName("project-name").build();
        kafkaProducer.sendMessage(topic, key, mapper.writeValueAsString(message));

    }
}
