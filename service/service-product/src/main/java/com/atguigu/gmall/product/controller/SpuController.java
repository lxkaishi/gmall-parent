package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-23
 */
@RestController
@RequestMapping("/admin/product")
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 带条件查询spu信息
     * @param page
     * @param limit
     * @param category3Id
     * @return
     */
    @GetMapping("/{page}/{limit}")
    public Result getSpuByCategoryId(@PathVariable("page") Long page,
                                     @PathVariable("limit") Long limit,
                                     @RequestParam("category3Id") Long category3Id){
        Page<SpuInfo> p = new Page<>(page,limit);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", category3Id);

        Page<SpuInfo> spuInfoPage = spuInfoService.page(p, wrapper);
        return Result.ok(spuInfoPage);
    }

    /**
     * 保存一个spu
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        //spuinfo的大保存
        spuInfoService.saveSpuinfo(spuInfo);
        return Result.ok();
    }

    /**
     * 获取spu图片
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){

        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuImage> images = spuImageService.list(wrapper);
        return Result.ok(images);
    }

    /**
     * 获取spu的销售属性和值
     * @param spuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getspuSaleAttrList(@PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrs);
    }
}
