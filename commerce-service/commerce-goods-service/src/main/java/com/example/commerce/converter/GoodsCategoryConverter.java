package com.example.commerce.converter;

import com.example.commerce.constant.GoodsCategory;

import javax.persistence.AttributeConverter;

public class GoodsCategoryConverter implements AttributeConverter<GoodsCategory, String> {

    @Override
    public String convertToDatabaseColumn(GoodsCategory attribute) {
        return attribute.getCode();
    }

    @Override
    public GoodsCategory convertToEntityAttribute(String dbData) {
        return GoodsCategory.of(dbData);
    }
}
