package com.example.commerce.entity;

import com.example.commerce.account.AddressInfo;
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
@Table(name = "t_ecommerce_address")
public class CommerceAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    /**
     * 转换 addressItem  -->  CommerceAddress
     */
    public static CommerceAddress to(Long userId, AddressInfo.AddressItem addressItem) {
        return CommerceAddress.builder()
                .userId(userId)
                .username(addressItem.getUsername())
                .phone(addressItem.getPhone())
                .province(addressItem.getProvince())
                .city(addressItem.getCity())
                .addressDetail(addressItem.getAddressDetail())
                .build();
    }

    /**
     * 转换 CommerceAddress  -->  AddressInfo.AddressItem
     */
    public AddressInfo.AddressItem toAddressItem() {
        return AddressInfo.AddressItem.builder()
                .id(id)
                .username(username)
                .phone(phone)
                .province(province)
                .city(city)
                .addressDetail(addressDetail)
                .createTime(createTime)
                .updateTime(updateTime)
                .build();
    }
}
