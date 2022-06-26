package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.vo.SkuValue;
import com.atguigu.gmall.product.biz.SpudeSkuSaleAttrBizService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@Service
public class SpudeSkuSaleAttrBizServiceImpl implements SpudeSkuSaleAttrBizService {

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Override
    public String getSpudeAllSkuSaleAttrAndValue(Long spuId) {
        List<SkuValue> SkuValues = spuSaleAttrMapper.getSpudeAllSkuSaleAttrAndValue(spuId);
        Map<String, String> jsonMap = new HashMap<>();

        for (SkuValue value : SkuValues) {
            String values = value.getSkuValues();
            String skuId = value.getSkuId();
            jsonMap.put(values,skuId);
        }

        return JSONs.toStr(jsonMap);
    }
}
