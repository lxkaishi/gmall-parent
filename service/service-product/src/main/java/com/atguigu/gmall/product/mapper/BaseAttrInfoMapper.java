package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domain.BaseAttrInfo
 */
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    List<BaseAttrInfo> getBaseAttrInfoWithValue(Long c1Id, Long c2Id, Long c3Id);
}




