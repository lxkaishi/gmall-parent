package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.service.BloomService;
import com.atguigu.gmall.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-30
 */
@Service
@Slf4j
public class BloomServiceImpl implements BloomService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuInfoService skuInfoService;
    private List<Long> skuIds;


    @Override
    public void initBloom() {

        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        if (filter.isExists()) {
            log.info("{} 布隆过滤器已经存在，跳过初始化", RedisConst.SKU_BLOOM_FILTER_NAME);
            return;
        }

        filter.tryInit(1000000, 0.000001);

        //3.1、 查出所有商品的id
        List<Long> skuIds = skuInfoService.getSkuIds();

        for (Long skuId : skuIds) {
            filter.add(skuId);
        }
        log.info("{} 布隆过滤器初始化完成：总计：{}", RedisConst.SKU_BLOOM_FILTER_NAME, skuIds.size());
    }

    @Override
    public void rebuildSkuBloom() {
        log.info("正在重建 {}布隆...",RedisConst.SKU_BLOOM_FILTER_NAME);
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        filter.delete();

        initBloom();
    }

}
