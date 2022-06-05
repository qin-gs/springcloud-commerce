package com.example.commerce.entity;

import com.example.commerce.constant.BrandCategory;
import com.example.commerce.constant.GoodsCategory;
import com.example.commerce.constant.GoodsStatus;
import com.example.commerce.converter.BrandCategoryConverter;
import com.example.commerce.converter.GoodsCategoryConverter;
import com.example.commerce.converter.GoodsStatusConverter;
import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_ecommerce_goods")
public class CommerceGoods {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 商品类型
     */
    @Column(name = "goods_category", nullable = false)
    @Convert(converter = GoodsCategoryConverter.class)
    private GoodsCategory goodsCategory;

    /**
     * 品牌分类
     */
    @Column(name = "brand_category", nullable = false)
    @Convert(converter = BrandCategoryConverter.class)
    private BrandCategory brandCategory;

    /**
     * 商品名称
     */
    @Column(name = "goods_name", nullable = false)
    private String goodsName;

    /**
     * 商品图片
     */
    @Column(name = "goods_pic", nullable = false)
    private String goodsPic;

    /**
     * 商品描述信息
     */
    @Column(name = "goods_description", nullable = false)
    private String goodsDescription;

    /**
     * 商品状态
     */
    @Column(name = "goods_status", nullable = false)
    @Convert(converter = GoodsStatusConverter.class)
    private GoodsStatus goodsStatus;

    /**
     * 商品价格
     */
    @Column(name = "price", nullable = false)
    private Integer price;

    /**
     * 供应量
     */
    @Column(name = "supply", nullable = false)
    private Long supply;

    /**
     * 商品库存
     */
    @Column(name = "inventory", nullable = false)
    private Long inventory;

    /**
     * 商品属性
     */
    @Column(name = "goods_property", nullable = false)
    private String goodsProperty;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    @CreatedDate
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    @LastModifiedDate
    private Date updateTime;


    private static ObjectMapper MAPPER = new ObjectMapper();

    /**
     * GoodsInfo  -->  CommerceGoods
     */
    public static CommerceGoods to(GoodsInfo goodsInfo) {
        try {
            return CommerceGoods.builder()
                    .goodsCategory(GoodsCategory.of(goodsInfo.getGoodsCategory()))
                    .brandCategory(BrandCategory.of(goodsInfo.getBrandCategory()))
                    .goodsName(goodsInfo.getGoodsName())
                    .goodsPic(goodsInfo.getGoodsPic())
                    .goodsDescription(goodsInfo.getGoodsDescription())
                    .goodsStatus(GoodsStatus.ONLINE)
                    .price(goodsInfo.getPrice())
                    .supply(goodsInfo.getSupply())
                    .inventory(goodsInfo.getSupply())
                    .goodsProperty(MAPPER.writeValueAsString(goodsInfo.getGoodsProperty()))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CommerceGoods  -->  GoodsInfo
     */
    public GoodsInfo toGoodsInfo() {
        try {
            return GoodsInfo.builder()
                    .id(this.getId())
                    .brandCategory(this.getBrandCategory().getCode())
                    .goodsName(this.getGoodsName())
                    .goodsPic(this.getGoodsPic())
                    .goodsDescription(this.getGoodsDescription())
                    .goodsStatus(this.getGoodsStatus().getStatus())
                    .price(this.getPrice())
                    .goodsProperty(MAPPER.readValue(this.getGoodsProperty(), GoodsInfo.GoodsProperty.class))
                    .supply(this.getSupply())
                    .inventory(this.getInventory())
                    .createTime(this.getCreateTime())
                    .updateTime(this.getUpdateTime())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CommerceGoods  -->  SimpleGoodsInfo
     */
    public SimpleGoodsInfo toSimple() {
        return SimpleGoodsInfo.builder()
                .id(this.getId())
                .goodsName(this.getGoodsName())
                .goodsPic(this.getGoodsPic())
                .price(this.getPrice())
                .build();
    }
}
