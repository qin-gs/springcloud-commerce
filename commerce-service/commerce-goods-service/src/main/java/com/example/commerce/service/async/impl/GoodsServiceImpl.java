package com.example.commerce.service.async.impl;

import com.example.commerce.common.TableId;
import com.example.commerce.constant.GoodsConstant;
import com.example.commerce.dao.CommerceGoodsDao;
import com.example.commerce.entity.CommerceGoods;
import com.example.commerce.goods.DeductGoodsInventory;
import com.example.commerce.goods.GoodsInfo;
import com.example.commerce.goods.SimpleGoodsInfo;
import com.example.commerce.service.IGoodsService;
import com.example.commerce.vo.PageSimpleGoodsInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements IGoodsService {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    private final StringRedisTemplate redisTemplate;
    private final CommerceGoodsDao commerceGoodsDao;

    public GoodsServiceImpl(StringRedisTemplate redisTemplate, CommerceGoodsDao commerceGoodsDao) {
        this.redisTemplate = redisTemplate;
        this.commerceGoodsDao = commerceGoodsDao;
    }

    @Override
    public List<GoodsInfo> getGoodsInfoByTableId(TableId tableId) {
        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(toList());
        log.info("详细商品信息 id: {}", ids);
        List<CommerceGoods> commerceGoods = IterableUtils.toList(commerceGoodsDao.findAllById(ids));
        return commerceGoods.stream().map(CommerceGoods::toGoodsInfo).collect(toList());
    }

    @Override
    public PageSimpleGoodsInfo getSimpleGoodsInfoByPage(int page) {
        // 分页不能从 redis 中查
        if (page <= 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<CommerceGoods> orderPage = commerceGoodsDao.findAll(pageable);

        // 是否还有更多页
        boolean hasMore = orderPage.getTotalPages() > page;
        return new PageSimpleGoodsInfo(orderPage.getContent().stream().map(CommerceGoods::toSimple).collect(toList()), hasMore);
    }

    @Override
    public List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(TableId tableId) {
        // 获取商品的简单信息，如果 redis 中查不到，去数据库中查
        List<Object> goodsIds = tableId.getIds().stream().map(id -> id.getId().toString()).collect(toList());
        List<Object> cachedSimpleGoodsInfos = redisTemplate.opsForHash().multiGet(GoodsConstant.COMMERCE_GOODS_DICT_KEY, goodsIds);

        // 如果 redis 中查到了，直接返回
        if (CollectionUtils.isNotEmpty(cachedSimpleGoodsInfos)) {
            if (cachedSimpleGoodsInfos.size() == goodsIds.size()) {
                log.info("从 redis 中查到了商品的简单信息: {}", goodsIds);
                return parseCacheGoodsInfo(cachedSimpleGoodsInfos);
            } else {
                // 没查到的部分，去数据库中查
                List<SimpleGoodsInfo> fromCache = parseCacheGoodsInfo(cachedSimpleGoodsInfos);
                // 找到 redis 中没有的
                Collection<Long> fromDbIds = CollectionUtils.subtract(
                        goodsIds.stream().map(o -> Long.valueOf(o.toString())).collect(toList()),
                        fromCache.stream().map(SimpleGoodsInfo::getId).collect(toList())
                );
                List<SimpleGoodsInfo> fromDb = queryGoodsFromDbAndCacheToRedis(new TableId(fromDbIds.stream().map(TableId.Id::new).collect(toList())));

                return new ArrayList<>(CollectionUtils.union(fromCache, fromDb));
            }
        } else {
            // redis 没查到，去数据库中查
            return queryGoodsFromDbAndCacheToRedis(tableId);
        }
    }

    private List<SimpleGoodsInfo> parseCacheGoodsInfo(List<Object> cacheSimpleGoodsInfo) {
        return cacheSimpleGoodsInfo.stream().map(o -> {
            try {
                return MAPPER.readValue(o.toString(), SimpleGoodsInfo.class);
            } catch (JsonProcessingException e) {
                log.error("解析缓存商品信息失败", e);
                return null;
            }
        }).collect(toList());
    }

    /**
     * 从数据库表查询，保存到 redis
     */
    private List<com.example.commerce.goods.SimpleGoodsInfo> queryGoodsFromDbAndCacheToRedis(TableId tableId) {
        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(toList());
        log.info("从数据库查到的详细商品信息 ids: {}", ids);

        // 将结果缓存
        List<CommerceGoods> commerceGoods = IterableUtils.toList(commerceGoodsDao.findAllById(ids));
        List<com.example.commerce.goods.SimpleGoodsInfo> simpleGoodsInfos = commerceGoods.stream().map(CommerceGoods::toSimple).collect(toList());

        Map<String, String> id2JsonObject = new HashMap<>(simpleGoodsInfos.size());
        simpleGoodsInfos.forEach(simpleGoodsInfo -> {
            try {
                id2JsonObject.put(simpleGoodsInfo.getId().toString(), MAPPER.writeValueAsString(simpleGoodsInfo));
            } catch (JsonProcessingException e) {
                log.error("缓存商品信息失败", e);
            }
        });
        redisTemplate.opsForHash().putAll(GoodsConstant.COMMERCE_GOODS_DICT_KEY, id2JsonObject);
        return simpleGoodsInfos;
    }

    @Override
    public boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories) {

        deductGoodsInventories.forEach(good -> {
            if (good.getCount() <= 0) {
                throw new RuntimeException("商品数量不能小于等于0");
            }
        });
        List<CommerceGoods> commerceGoods = IterableUtils.toList(commerceGoodsDao.findAllById(
                deductGoodsInventories.stream()
                        .map(DeductGoodsInventory::getGoodsId)
                        .collect(toList())
        ));
        if (CollectionUtils.isEmpty(commerceGoods)) {
            throw new RuntimeException("商品不存在");
        }
        if (commerceGoods.size() != deductGoodsInventories.size()) {
            log.error("商品数量不正确，查询出来的数量: {}， 传递的数量: {}", commerceGoods.size(), deductGoodsInventories.size());
            throw new RuntimeException("商品数量不正确");
        }
        Map<Long, DeductGoodsInventory> goodsInventoryMap = deductGoodsInventories.stream()
                .collect(Collectors.toMap(DeductGoodsInventory::getGoodsId, Function.identity()));

        commerceGoods.forEach(good -> {
            Long curr = good.getInventory();
            Long less = goodsInventoryMap.get(good.getId()).getCount();
            if (curr < less) {
                log.error("商品库存不足，商品id: {}, 当前库存: {}, 减少的库存: {}", good.getId(), curr, less);
                throw new RuntimeException("商品库存不足");
            }
            good.setInventory(curr - less);
            log.info("商品库存减少，商品id: {}, 当前库存: {}, 减少的库存: {}", good.getId(), curr, less);
        });
        commerceGoodsDao.saveAll(commerceGoods);
        log.info("商品库存减少成功");

        return true;
    }
}
