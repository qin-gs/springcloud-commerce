package com.example.commerce.service.async.impl;

import com.example.commerce.constant.CommonConstant;
import com.example.commerce.constant.GoodsConstant;
import com.example.commerce.dao.CommerceGoodsDao;
import com.example.commerce.entity.CommerceGoods;
import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.service.async.IAsyncService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AsyncServiceImpl implements IAsyncService {

    private ObjectMapper mapper = new ObjectMapper();
    private final CommerceGoodsDao commerceGoodsDao;
    private final StringRedisTemplate redisTemplate;

    public AsyncServiceImpl(CommerceGoodsDao commerceGoodsDao, StringRedisTemplate redisTemplate) {
        this.commerceGoodsDao = commerceGoodsDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 使用指定的线程池执行异步任务
     * 1. 将商品信息保存到数据表
     * 2. 更新商品缓存
     */
    @Async("getAsyncExecutor")
    @Override
    public void asyncImportGoods(List<GoodsInfo> goodsInfos, String taskId) {

        log.info("开始执行异步任务, taskId: {}，异步任务线程池：{}", taskId, Thread.currentThread().getName());
        StopWatch watch = StopWatch.createStarted();

        // 如果存在重复的商品，直接返回
        boolean isIllegal = false;
        // 将商品信息字段组合到一起
        Set<String> goodsJoinInfos = new HashSet<>(goodsInfos.size());
        List<GoodsInfo> filterGoodsInfo = new ArrayList<>(goodsInfos.size());

        for (GoodsInfo goodsInfo : goodsInfos) {
            if (goodsInfo.getPrice() <= 0 || goodsInfo.getSupply() <= 0) {
                log.info("商品信息不合法，商品信息：{}", goodsInfo);
            }
            String joinInfo = String.format("%s,%s,%s", goodsInfo.getGoodsCategory(), goodsInfo.getBrandCategory(), goodsInfo.getGoodsName());
            if (goodsJoinInfos.contains(joinInfo)) {
                isIllegal = true;
                log.info("商品信息重复，商品信息：{}", goodsInfo);
            }
            goodsJoinInfos.add(joinInfo);
            filterGoodsInfo.add(goodsInfo);
        }

        // 如果存在重复的 或 没有商品，直接返回
        if (isIllegal || CollectionUtils.isEmpty(filterGoodsInfo)) {
            watch.stop();
            log.error("有重复商品 或 商品数量为 0，taskId: {}, 商品数量: {}, 耗时: {}s", taskId, filterGoodsInfo.size(), watch.getTime(TimeUnit.SECONDS));
            return;
        }

        List<CommerceGoods> commerceGoods = filterGoodsInfo.stream().map(CommerceGoods::to).collect(Collectors.toList());
        List<CommerceGoods> targetGoods = new ArrayList<>(commerceGoods.size());

        // 保存之前先判断是否存在重复商品
        commerceGoods.forEach(goods -> {
            if (commerceGoodsDao.findFirstByGoodsCategoryAndBrandCategoryAndGoodsName(goods.getGoodsCategory(), goods.getBrandCategory(), goods.getGoodsName()).orElse(null) != null) {
                return;
            }
            targetGoods.add(goods);
        });

        List<CommerceGoods> savedGoods = IterableUtils.toList(commerceGoodsDao.saveAll(targetGoods));
        // 将入库商品信息同步到 redis
        watch.stop();
        log.info("保存商品信息到 redis, taskId: {}, 商品数量: {}, 耗时: {}s", taskId, savedGoods.size(), watch.getTime(TimeUnit.SECONDS));

        savedNewGoodsInfoToRedis(savedGoods);
    }

    /**
     * 将新增的商品 (保存到数据库表中的) 信息同步到 redis；
     * 只保存简单信息 dict
     */
    private void savedNewGoodsInfoToRedis(List<CommerceGoods> savedGoods) {
        List<SimpleGoodsInfo> simpleGoodsInfos = savedGoods.stream().map(CommerceGoods::toSimple).collect(Collectors.toList());
        Map<String, Object> goodsMap = simpleGoodsInfos.stream()
                .collect(
                        Collectors.toMap(
                                simpleGoodsInfo -> simpleGoodsInfo.getId().toString(),
                                simpleGoodsInfo -> {
                                    try {
                                        return mapper.writeValueAsString(simpleGoodsInfo);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
        redisTemplate.opsForHash().putAll(GoodsConstant.COMMERCE_GOODS_DICT_KEY, goodsMap);
    }
}
