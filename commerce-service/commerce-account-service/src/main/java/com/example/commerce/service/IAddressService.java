package com.example.commerce.service;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.common.TableId;

/**
 * 用户地址相关服务接口
 */
public interface IAddressService {

    /**
     * 创建用户地址信息
     */
    TableId createAddressInfo(AddressInfo addressInfo);

    /**
     * 获取当前用户的地址信息 (当前用户 id 通过 AccessContext 获取)
     */
    AddressInfo getCurrentAddressInfo();

    /**
     * 根据 id 获取用户地址信息，id 是 CommerceAddress 主键
     */
    AddressInfo getAddressInfoById(Long id);

    /**
     * 根据 TableId 获取用户地址信息
     */
    AddressInfo getAddressByTableId(TableId tableId);

}
