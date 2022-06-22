package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    List<BaseAttrInfo> getBaseAttrInfoWithValue(@Param("c1Id") Long c1Id, @Param("c2Id") Long c2Id, @Param("c3Id") Long c3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 修改平台属性名和值
     * @param baseAttrInfo
     */
    void updateAttrInfo(BaseAttrInfo baseAttrInfo);
}
