package com.example.commerce.service;

import com.example.commerce.common.TableId;
import com.example.commerce.goods.DeductGoodsInventory;
import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.vo.PageSimpleGoodsInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IGoodsServiceTest {

    @Autowired
    private IGoodsService service;

    @Test
    public void getGoodsInfoByTableId() {
        List<Long> ids = Arrays.asList(10L, 11L, 12L);
        List<TableId.Id> tableIds = ids.stream().map(TableId.Id::new)
                .collect(Collectors.toList());
        List<GoodsInfo> goodsInfos = service.getGoodsInfoByTableId(new TableId(tableIds));
        System.out.println("goodsInfos = " + goodsInfos);
    }

    @Test
    public void getSimpleGoodsInfoByPage() {
        PageSimpleGoodsInfo info = service.getSimpleGoodsInfoByPage(1);
        System.out.println("info = " + info);
    }

    @Test
    public void getSimpleGoodsInfoByTableId() {
        List<Long> ids = Arrays.asList(10L, 11L, 12L);
        List<TableId.Id> tableIds = ids.stream().map(TableId.Id::new)
                .collect(Collectors.toList());
        List<SimpleGoodsInfo> info = service.getSimpleGoodsInfoByTableId(new TableId(tableIds));
        System.out.println("info = " + info);
    }

    @Test
    public void deductGoodsInventory() {
        List<DeductGoodsInventory> deductGoodsInventories = Arrays.asList(
                new DeductGoodsInventory(10L, 10L),
                new DeductGoodsInventory(11L, 10L)
        );
        boolean b = service.deductGoodsInventory(deductGoodsInventories);
        System.out.println("b = " + b);
    }
}