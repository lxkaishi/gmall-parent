package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.model.vo.user.UserAuth;
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
import java.util.Date;
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

        //添到布隆过滤器中
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        filter.add(id);
    }

    @Override
    public void upSku(Long skuId) {
        //1、数据库修改状态
        skuInfoMapper.updateSaleStatus(skuId,1);
        //2、数据保存到es中
        Goods goods = this.getGoodsInfoBySkuId(skuId);
        //3、远程调用检索服务进行上架
        searchFeignClient.upGoods(goods);
    }

    @Override
    public void downSku(Long skuId) {
        //1、修改数据库状态
        skuInfoMapper.updateSaleStatus(skuId,0);

        //2、链接es远程下架
        searchFeignClient.downGoods(skuId);
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

    @Override
    public Goods getGoodsInfoBySkuId(Long skuId) {
        //es中一个sku的详情。对应一个 Goods

        Goods goods = skuInfoMapper.getGoodsInfoBySkuId(skuId);
        return goods;
    }

    @Override
    public CartInfo getCartInfoBySkuId(Long skuId) {

        CartInfo cartInfo = new CartInfo();
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        if(userAuth.getUserId()!=null){
            cartInfo.setUserId(userAuth.getUserId().toString());
        }else {
            cartInfo.setUserId(userAuth.getTempId());
        }

        cartInfo.setSkuId(skuId);
        cartInfo.setId(cartInfo.getId());

        //查价格
        BigDecimal skuPrice = skuInfoMapper.getSkuPrice(skuId);
        cartInfo.setCartPrice(skuPrice);

        cartInfo.setSkuNum(null);

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        //默认被选中
        cartInfo.setIsChecked(1);

        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());

        //商品实时价格
        cartInfo.setSkuPrice(skuPrice);  //未来价格发生变动的话，需要改购物车

        cartInfo.setCouponInfoList(null);




        return cartInfo;

    }
}




