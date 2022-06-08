package com.example.commerce.stream.my;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 自定义输出信道
 */
public interface MySource {

    String OUTPUT = "myOutput";

    /**
     * 向通道发送消息，指定初始信道的名称 (需要声明到配置文件)
     */
    @Output(MySource.OUTPUT)
    MessageChannel myOutput();
}
