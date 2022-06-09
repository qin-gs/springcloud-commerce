package com.example.commerce.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_ecommerce_order")
@EntityListeners(AuditingEntityListener.class)
public class CommerceOrder {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 地址id
     */
    @Column(name = "address_id", nullable = false)
    private Long addressId;

    /**
     * 订单详情
     */
    @Column(name = "order_detail", nullable = false)
    private String orderDetail;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    public CommerceOrder(Long userId, Long addressId, String orderDetail) {
        this.userId = userId;
        this.addressId = addressId;
        this.orderDetail = orderDetail;
    }
}
