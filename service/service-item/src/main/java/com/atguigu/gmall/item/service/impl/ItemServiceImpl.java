package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClent;
import com.atguigu.gmall.item.component.CacheService;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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


    @Override
    public SkuDetailVo getSkuDetail(Long skuId) {

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


    public SkuDetailVo getItemDetailFromRpc(Long skuId) {

        // 1.
        SkuDetailVo vo = new SkuDetailVo();

        Result<SkuInfo> skuInfo = skuFeignClent.getSkuInfo(skuId);
        SkuInfo info = skuInfo.getData();
        vo.setSkuInfo(info);

        // 2.
        Long category3Id = info.getCategory3Id();
        // 根据三级分类id查询完整分类信息
        Result<CategoryView> categoryView = skuFeignClent.getCategoryViewDo(category3Id);
        vo.setCategoryView(categoryView.getData());


        vo.setPrice(info.getPrice());

        Long spuId = info.getSpuId();
        Result<List<SpuSaleAttr>> saleAttr = skuFeignClent.getSaleAttr(skuId, spuId);
        if (saleAttr.isOk()) {
            vo.setSpuSaleAttrList(saleAttr.getData());
        }

        Result<String> value = skuFeignClent.getSpudeAllSkuSaleAttrAndValue(spuId);
        vo.setValuesSkuJson(value.getData());


        return vo;
    }
}
