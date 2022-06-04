package com.example.commerce.converter;

import com.example.commerce.constant.BrandCategory;

import javax.persistence.AttributeConverter;

public class BrandCategoryConverter implements AttributeConverter<BrandCategory, String> {

    @Override
    public String convertToDatabaseColumn(BrandCategory attribute) {
        return attribute.getCode();
    }

    @Override
    public BrandCategory convertToEntityAttribute(String dbData) {
        return BrandCategory.of(dbData);
    }
}
