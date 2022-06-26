package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@FeignClient("service-product")
@RequestMapping("/rpc/inner/product")
public interface SkuFeignClent {

    @GetMapping("/skuinfo/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/categoryview/{c3Id}")
    Result<CategoryView> getCategoryViewDo(@PathVariable("c3Id")Long c3Id);

    @GetMapping("/sku/saleattr/{skuId}/{spuId}")
    Result<List<SpuSaleAttr>> getSaleAttr(@PathVariable("skuId")Long skuId,
                                                 @PathVariable("spuId")Long spuId);

    @GetMapping("/spu/skus/saleattrvalue/json/{spuId}")
    Result<String> getSpudeAllSkuSaleAttrAndValue(@PathVariable("spuId") Long spuId);
}
