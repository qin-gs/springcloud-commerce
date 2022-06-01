package com.example.commerce.service.impl;

import com.example.commerce.account.AddressInfo;
import com.example.commerce.account.TableId;
import com.example.commerce.dao.CommerceAddressDao;
import com.example.commerce.entity.CommerceAddress;
import com.example.commerce.filter.AccessContext;
import com.example.commerce.service.IAddressService;
import com.example.commerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 用户地址相关接口实现
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AddressServiceImpl implements IAddressService {

    private final CommerceAddressDao addressDao;

    public AddressServiceImpl(CommerceAddressDao commerceAddressDao) {
        this.addressDao = commerceAddressDao;
    }

    /**
     * 存储多个地址信息
     */
    @Override
    public TableId createAddressInfo(AddressInfo addressInfo) {

        // 不通过参数获取用户 id
        LoginUserInfo userInfo = AccessContext.getLoginUserInfo();
        List<CommerceAddress> addresses = addressInfo.getAddressItems().stream()
                .map(item -> CommerceAddress.to(userInfo.getId(), item))
                .collect(toList());

        // 保存到地址表将返回记录给调用方 (返回值是有 id 的)
        List<CommerceAddress> savedAddresses = addressDao.saveAll(addresses);
        List<Long> ids = savedAddresses.stream()
                .map(CommerceAddress::getId)
                .collect(toList());

        log.info("保存地址信息成功，用户id：{}, 地址id：{}", userInfo.getId(), ids);

        return new TableId(ids.stream().map(TableId.Id::new).collect(toList()));
    }

    @Override
    public AddressInfo getCurrentAddressInfo() {
        LoginUserInfo userInfo = AccessContext.getLoginUserInfo();
        // 根据用户 id 查询用户地址信息
        List<CommerceAddress> addresses = addressDao.findAllByUserId(userInfo.getId());
        List<AddressInfo.AddressItem> addressItems = addresses.stream()
                .map(CommerceAddress::toAddressItem)
                .collect(toList());
        return new AddressInfo(userInfo.getId(), addressItems);
    }

    @Override
    public AddressInfo getAddressInfoById(Long id) {
        CommerceAddress commerceAddress = addressDao.findById(id).orElse(null);
        if (commerceAddress == null) {
            throw new RuntimeException("地址信息不存在");
        }
        return new AddressInfo(
                commerceAddress.getUserId(),
                Collections.singletonList(commerceAddress.toAddressItem())
        );
    }

    @Override
    public AddressInfo getAddressByTableId(TableId tableId) {
        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(toList());
        log.info("查询地址信息，地址id：{}", ids);
        List<CommerceAddress> addresses = addressDao.findAllById(ids);
        if (CollectionUtils.isEmpty(addresses)) {
            return new AddressInfo(-1L, Collections.emptyList());
        }
        List<AddressInfo.AddressItem> addressItems = addresses.stream()
                .map(CommerceAddress::toAddressItem)
                .collect(toList());
        return new AddressInfo(addresses.get(0).getUserId(), addressItems);
    }
}
