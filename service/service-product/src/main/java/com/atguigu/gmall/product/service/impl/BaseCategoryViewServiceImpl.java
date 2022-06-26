package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.BaseCategoryViewService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class BaseCategoryViewServiceImpl extends ServiceImpl<BaseCategoryViewMapper, CategoryViewDo>
    implements BaseCategoryViewService{

    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    @Override
    public CategoryViewDo getViewByC3Id(Long c3Id) {

        QueryWrapper<CategoryViewDo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", c3Id);
        CategoryViewDo viewDo = baseCategoryViewMapper.selectOne(wrapper);
        return viewDo;
    }
}




