package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@ToString
@Slf4j
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageService skuImageService;
    @Autowired
    SkuAttrValueService skuAttrValueService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Autowired
    RedissonClient redissonClient;

    static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        log.info("sku?????????????????????{}",skuInfo);
        save(skuInfo);
        Long id = skuInfo.getId();

        List<SkuImage> imageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : imageList) {
            skuImage.setSkuId(id);
        }
        skuImageService.saveBatch(imageList);

        List<SkuAttrValue> attrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : attrValueList) {
            skuAttrValue.setSkuId(id);
        }
        skuAttrValueService.saveBatch(attrValueList);

        List<SkuSaleAttrValue> saleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : saleAttrValueList) {
            skuSaleAttrValue.setSkuId(id);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(saleAttrValueList);
        log.info("sku??????????????????????????????skuId???{}",id);

        //????????????????????????
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        filter.add(id);
    }

    @Override
    public void upSku(Long skuId) {
        //1????????????????????????
        skuInfoMapper.updateSaleStatus(skuId,1);
        //2??????????????????es???
        Goods goods = this.getGoodsInfoBySkuId(skuId);
        //3???????????????????????????????????????
        searchFeignClient.upGoods(goods);
    }

    @Override
    public void downSku(Long skuId) {
        //1????????????????????????
        skuInfoMapper.updateSaleStatus(skuId,0);

        //2?????????es????????????
        searchFeignClient.downGoods(skuId);
    }

    @Override
    public List<Long> getSkuIds() {
        return skuInfoMapper.getSkuIds();
    }

    @Override
    public void updateSkuInfo(SkuInfo skuInfo) {
        //1???????????????

        //2??????????????????
        //1???????????????   80% ???ok
        redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId());
        //2???????????????   99.99% ???ok
        //????????????????????????????????????
        threadPool.schedule(()->redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId()),10, TimeUnit.SECONDS);
        //????????????
        //????????????????????????????????? redis??????????????????
        //redis?????????????????????????????????
        //1??????
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        return skuInfoMapper.getSkuPrice(skuId);
    }

    @Override
    public Goods getGoodsInfoBySkuId(Long skuId) {
        //es?????????sku???????????????????????? Goods

        Goods goods = skuInfoMapper.getGoodsInfoBySkuId(skuId);
        return goods;
    }
}




