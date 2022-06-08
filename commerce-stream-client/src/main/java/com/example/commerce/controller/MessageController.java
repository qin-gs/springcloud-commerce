package com.example.commerce.controller;

import com.example.commerce.stream.DefaultReceiveService;
import com.example.commerce.stream.DefaultSendService;
import com.example.commerce.stream.my.MyReceiveService;
import com.example.commerce.stream.my.MySendService;
import com.example.commerce.vo.MessageObj;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 构建消息驱动
 */
@Slf4j
@RequestMapping("/message")
@RestController
public class MessageController {

    private DefaultSendService defaultSendService;
    private DefaultReceiveService defaultReceiveService;
    private MySendService mySendService;
    private MyReceiveService myReceiveService;

    public MessageController(DefaultSendService defaultSendService, DefaultReceiveService defaultReceiveService,
                             MySendService mySendService, MyReceiveService myReceiveService) {
        this.defaultSendService = defaultSendService;
        this.defaultReceiveService = defaultReceiveService;
        this.mySendService = mySendService;
        this.myReceiveService = myReceiveService;
    }

    /**
     * 使用默认信道发送接收消息
     */
    @GetMapping("default")
    public void defaultSend() throws JsonProcessingException {
        defaultSendService.sendMessage(MessageObj.defaultMessage());
    }

    /**
     * 使用自定义信道发送消息
     */
    @GetMapping("my")
    public void mySend() throws JsonProcessingException {
        mySendService.send(MessageObj.defaultMessage());
    }
}
