package com.example.commerce.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 物流表
 */
@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_ecommerce_logistics")
public class CommerceLogistics {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 用户 id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 订单 is
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * 用户地址 id
     */
    @Column(name = "address_id", nullable = false)
    private Long addressId;

    /**
     * 备注信息
     */
    @Column(name = "extraInfo", nullable = false)
    private String extraInfo;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Data createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private Data updateTime;

    public CommerceLogistics(Long userId, Long orderId, Long addressId, String extraInfo) {
        this.userId = userId;
        this.orderId = orderId;
        this.addressId = addressId;
        this.extraInfo = StringUtils.isNotBlank(extraInfo) ? extraInfo : "{}";
    }
}
