package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
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
public class SaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    /**
     * 获取所有的销售属性名列表
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result getBaseSaleAttrList(){

        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrService.list();
        return Result.ok(baseSaleAttrs);
    }


}
