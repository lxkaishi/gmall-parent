package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.vo.SkuDetailVo;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
public interface ItemService {
    SkuDetailVo getSkuDetail(Long skuId);
}
