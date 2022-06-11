package com.example.commerce.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 物流信息通信信道
 */
public interface LogisticsSink {

    String INPUT = "logisticsInput";

    @Input(LogisticsSink.INPUT)
    SubscribableChannel logisticsInput();
}
