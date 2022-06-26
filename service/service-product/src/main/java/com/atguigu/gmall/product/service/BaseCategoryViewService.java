package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface BaseCategoryViewService extends IService<CategoryViewDo> {

    /**
     * 根据三级分类id去 base_category_view 视图中查询精准分类路径
     * @param c3Id
     * @return
     */
    CategoryViewDo getViewByC3Id(Long c3Id);
}
