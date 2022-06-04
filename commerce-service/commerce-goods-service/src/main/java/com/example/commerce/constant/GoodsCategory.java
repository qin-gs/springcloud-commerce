package com.example.commerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum GoodsCategory {

    /**
     * 电器
     */
    DIAN_QI("10001", "电器"),
    /**
     * 家具
     */
    JIA_JU("10002", "家具"),
    /**
     * 服饰
     */
    FU_SHI("10003", "服饰"),
    /**
     * 母婴
     */
    MY_YIN("10004", "母婴"),
    /**
     * 食品
     */
    SHI_PIN("10005", "食品"),
    /**
     * 图书
     */
    TU_SHU("10006", "图书"),
    ;

    /**
     * 商品类别编码
     */
    private final String code;

    /**
     * 商品类别描述信息
     */
    private final String description;

    /**
     * 根据 code 获取到 GoodsCategory
     */
    public static GoodsCategory of(String code) {

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(goodsCategory -> goodsCategory.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("不支持的商品类别编码：" + code));
    }
}
