package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClent;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    SkuFeignClent skuFeignClent;

    @Autowired
    CacheService cacheService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Cache(key = RedisConst.SKU_INFO_CACHE_KEY_PREFIX+"#{#params[0]}",
            bloomName = RedisConst.SKU_BLOOM_FILTER_NAME,
            bloomIf = "#{#params[0]}",
            ttl = RedisConst.SKU_INFO_CACHE_TIMEOUT)
    @Override
    public SkuDetailVo getSkuDetail(Long skuId){
        return getItemDetailFromRpc(skuId);
    }

    @Override
    public void incrHotScore(Long skuId) {
//1、积攒
        Long increment = redisTemplate.opsForValue().increment(RedisConst.SKU_HOTSCORE+skuId);
        if(increment % 100 == 0){
            //更新频率不要频繁。异步
            //理论上线程超过核心数的2倍，就再多就没意义。每一个异步不能上来就开线程
            //线程池： 16  32  queue
            CompletableFuture.runAsync(()->{
                searchFeignClient.incrHotScore(skuId,increment);
            },threadPoolExecutor);
        }
    }

    public SkuDetailVo getSkuDetailV1(Long skuId){

        String cacheKey = RedisConst.SKU_INFO_CACHE_KEY_PREFIX + skuId;
        SkuDetailVo data = cacheService.getData(cacheKey, SkuDetailVo.class);
        if (data == null) {
            log.info("sku:{} 详情- 缓存不命中，准备回源..正在检索布隆是否存在这个商品");
            RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
            if (!filter.contains(skuId)) {
                log.info("sku:{} 布隆觉得没有，请求无法穿透...");
                return null;
            }
            //2.1、 缓存中没有。准备回源。
            //2.2、 加分布式锁  lock:sku:info:49
            RLock lock = redissonClient.getLock(RedisConst.SKU_INFO_LOCK_PREFIX + skuId);
            //2.3、加锁
            boolean tryLock = lock.tryLock();
            //2.4、获得锁
            if(tryLock){
                //2.7、准备回源
                SkuDetailVo detail = getItemDetailFromRpc(skuId);
                //2.8、缓存一份
                cacheService.saveData(cacheKey,detail,RedisConst.SKU_INFO_CACHE_TIMEOUT,TimeUnit.MILLISECONDS);
                lock.unlock();
                return detail;
            }else {
                //2.5、没获得锁
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    //2.6、直接查缓存即可
                    return  cacheService.getData(cacheKey, SkuDetailVo.class);
                } catch (InterruptedException e) {

                }
            }
        }

        //3、缓存中有这个数据，直接返回
        return data;
    }


    @SneakyThrows
    public SkuDetailVo getItemDetailFromRpc(Long skuId){

        // 1.
        SkuDetailVo vo = new SkuDetailVo();

        CompletableFuture<SkuInfo> baseInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> skuInfo = skuFeignClent.getSkuInfo(skuId);
            SkuInfo info = skuInfo.getData();
            vo.setSkuInfo(info);
            return info;
        });


        CompletableFuture<Void> categoryFuture = baseInfoFuture.thenAcceptAsync(info -> {
            // 2.
            Long category3Id = info.getCategory3Id();
            // 根据三级分类id查询完整分类信息
            Result<CategoryView> categoryView = skuFeignClent.getCategoryViewDo(category3Id);
            vo.setCategoryView(categoryView.getData());
        });


        CompletableFuture<Void> priceFuture = baseInfoFuture.thenAcceptAsync(info -> {
            vo.setPrice(info.getPrice());
        });


        CompletableFuture<Void> saleAttrFuture = baseInfoFuture.thenAcceptAsync(info -> {
            Long spuId = info.getSpuId();
            Result<List<SpuSaleAttr>> saleAttr = skuFeignClent.getSaleAttr(skuId, spuId);
            if (saleAttr.isOk()) {
                vo.setSpuSaleAttrList(saleAttr.getData());
            }
        });


        CompletableFuture<Void> skuOtherFuture = baseInfoFuture.thenAcceptAsync(info -> {
            Result<String> value = skuFeignClent.getSpudeAllSkuSaleAttrAndValue(info.getSpuId());
            vo.setValuesSkuJson(value.getData());
        });

        CompletableFuture.allOf(skuOtherFuture,saleAttrFuture,priceFuture,categoryFuture).get();

        return vo;
    }


}
