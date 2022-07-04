package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-25
 */
@Service
public class CategoryServiceImpl implements CategoryBizService {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;

    @Override
    @Cache(key = "categorys")
    public List<CategoryVo> getCategorys() {
        List<CategoryVo> vos = baseCategory1Mapper.getCategorys();
        return vos;
    }
}
