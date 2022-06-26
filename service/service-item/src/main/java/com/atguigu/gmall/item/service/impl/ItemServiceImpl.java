package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClent;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    SkuFeignClent skuFeignClent;

    @Override
    public SkuDetailVo getSkuDetail(Long skuId) {

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
