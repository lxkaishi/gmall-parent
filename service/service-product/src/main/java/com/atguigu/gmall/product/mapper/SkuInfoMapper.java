package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domain.SkuInfo
 */
@Repository
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    void updateSaleStatus(Long skuId, @Param("status") int status);

    /**
     * @return
     */
    List<Long> getSkuIds();

    BigDecimal getSkuPrice(@Param("skuId") Long skuId);

    Goods getGoodsInfoBySkuId(@Param("skuId") Long skuId);
}




