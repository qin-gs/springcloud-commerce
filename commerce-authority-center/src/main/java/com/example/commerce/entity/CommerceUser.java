package com.example.commerce.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 主动更新两个时间
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_commerce_user")
@EntityListeners(AuditingEntityListener.class)
public class CommerceUser implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * 密码
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 额外的信息
     */
    @Column(name = "extra_info", nullable = false)
    private String extraInfo;

    /**
     * 创建时间，自动更新
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    /**
     * 更新时间，自动更新
     */
    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

}
