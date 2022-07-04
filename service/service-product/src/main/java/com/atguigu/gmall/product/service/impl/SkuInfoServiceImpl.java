package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
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

    static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        log.info("sku信息正在保存：{}",skuInfo);
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
        log.info("sku信息保存成功：生成的skuId：{}",id);
    }

    @Override
    public void upSku(Long skuId) {
        //TODO 连接ES保存这个商品数据
        skuInfoMapper.updateSaleStatus(skuId,1);
    }

    @Override
    public void downSku(Long skuId) {
        //TODO 连接ES删除这个商品数据
        skuInfoMapper.updateSaleStatus(skuId,0);
    }

    @Override
    public List<Long> getSkuIds() {
        return skuInfoMapper.getSkuIds();
    }

    @Override
    public void updateSkuInfo(SkuInfo skuInfo) {
        //1、改数据库

        //2、双删缓存。
        //1）、立即删   80% 都ok
        redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId());
        //2）、延迟删   99.99% 都ok
        //拿到一个延迟任务的线程池
        threadPool.schedule(()->redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId()),10, TimeUnit.SECONDS);
        //立即结束
        //兜底：数据有过期时间。 redis怎么删数据？
        //redis怎么淘汰这些过期数据？
        //1）、
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        return skuInfoMapper.getSkuPrice(skuId);
    }
}




