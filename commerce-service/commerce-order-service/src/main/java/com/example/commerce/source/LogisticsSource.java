package com.example.commerce.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 自定义物流消息通信信道
 */
public interface LogisticsSource {

    /**
     * 输出信道名称
     */
    String OUTPUT = "logisticsOutput";

    /**
     * 信道
     */
    @Output(LogisticsSource.OUTPUT)
    MessageChannel logisticsOutput();
}
