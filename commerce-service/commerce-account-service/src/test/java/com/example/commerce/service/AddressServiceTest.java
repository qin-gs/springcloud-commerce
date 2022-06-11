package com.example.commerce.service;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.common.TableId;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

/**
 * 用户地址功能测试
 */
@Slf4j
public class AddressServiceTest extends BaseTest {

    @Autowired
    private IAddressService service;

    @Test
    public void createAddressInfo() {
        AddressInfo.AddressItem item = AddressInfo.AddressItem.builder()
                .username("qqq")
                .phone("123456789")
                .province("山东省")
                .city("济南市")
                .addressDetail("高新区")
                .build();
        log.info("添加地址：{}", service.createAddressInfo(new AddressInfo(userInfo.getId(), Collections.singletonList(item))));
    }

    @Test
    public void getCurrentAddressInfo() {
        AddressInfo info = service.getCurrentAddressInfo();
        log.info("当前地址：{}", info);
    }

    @Test
    public void getAddressInfoById() {
        AddressInfo info = service.getAddressInfoById(1L);
        log.info("根据 id 获取地址：{}", info);
    }

    @Test
    public void getAddressByTableId() {
        AddressInfo info = service.getAddressByTableId(new TableId(Collections.singletonList(new TableId.Id(1L))));
        log.info("根据 table id 获取地址：{}", info);
    }
}
