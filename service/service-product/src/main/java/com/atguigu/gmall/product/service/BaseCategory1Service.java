package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author lxstart
 * @create 2022-06-21 16:05
 */
public interface BaseCategory1Service extends IService<BaseCategory1> {
    List<BaseCategory2> getCategory2(Long category1Id);
}
