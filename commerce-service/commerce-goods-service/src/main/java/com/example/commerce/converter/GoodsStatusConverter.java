package com.example.commerce.converter;

import com.example.commerce.constant.GoodsStatus;

import javax.persistence.AttributeConverter;

/**
 * 商品状态转换
 */
public class GoodsStatusConverter implements AttributeConverter<GoodsStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GoodsStatus attribute) {
        return attribute.getStatus();
    }

    @Override
    public GoodsStatus convertToEntityAttribute(Integer dbData) {
        return GoodsStatus.of(dbData);
    }
}
