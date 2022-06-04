package com.example.commerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 品牌分类
 */
@Getter
@AllArgsConstructor
public enum BrandCategory {

    /**
     * 品牌A
     */
    BRAND_A("20001", "品牌A"),
    /**
     * 品牌B
     */
    BRAND_B("20002", "品牌B"),
    /**
     * 品牌C
     */
    BRAND_C("20003", "品牌C"),
    /**
     * 品牌D
     */
    BRAND_D("20004", "品牌D"),
    /**
     * 品牌E
     */
    BRAND_E("20005", "品牌E"),
    ;

    /**
     * 品牌分类编码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String description;

    /**
     * 根据 code 获取枚举
     */
    public static BrandCategory of(String code) {
        Objects.requireNonNull(code, "code 不能为空");

        return Arrays.stream(values())
                .filter(brandCategory -> brandCategory.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("品牌分类不存在 " + code));
    }
}
