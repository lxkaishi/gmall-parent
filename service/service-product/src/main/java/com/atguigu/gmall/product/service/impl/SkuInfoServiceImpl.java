package com.atguigu.gmall.product.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

}




