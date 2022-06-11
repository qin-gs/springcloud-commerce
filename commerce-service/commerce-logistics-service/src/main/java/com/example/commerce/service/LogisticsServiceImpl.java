package com.example.commerce.service;

import com.example.commerce.dao.CommerceLogisticsDao;
import com.example.commerce.entity.CommerceLogistics;
import com.example.commerce.sink.LogisticsSink;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
@EnableBinding(LogisticsSink.class)
public class LogisticsServiceImpl {

    private final CommerceLogisticsDao commerceLogisticsDao;
    private final ObjectMapper mapper;

    public LogisticsServiceImpl(CommerceLogisticsDao commerceLogisticsDao, ObjectMapper mapper) {
        this.commerceLogisticsDao = commerceLogisticsDao;
        this.mapper = mapper;
    }

    /**
     * 订阅监听订单微服务发送的物流信息
     */
    @StreamListener("logisticsInput")
    public void consumeLogisticsMessage(@Payload Object payload) throws JsonProcessingException {
        log.info("收到物流信息：{}", payload);
        CommerceLogistics message = mapper.readValue(payload.toString(), CommerceLogistics.class);

        CommerceLogistics logistics = commerceLogisticsDao.save(
                new CommerceLogistics(
                        message.getUserId(),
                        message.getOrderId(),
                        message.getAddressId(),
                        message.getExtraInfo()
                ));
        log.info("保存物流信息成功: {}", logistics);
    }
}
