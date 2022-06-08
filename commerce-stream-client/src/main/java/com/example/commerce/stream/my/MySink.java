package com.example.commerce.stream.my;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 自定义输入信道
 */
public interface MySink {

    String INPUT = "myInput";

    /**
     * 输入信道的名称 (需要声明到配置文件)
     */
    @Input(MySink.INPUT)
    SubscribableChannel myInput();
}
