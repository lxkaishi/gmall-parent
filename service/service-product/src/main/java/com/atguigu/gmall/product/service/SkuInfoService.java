package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 */
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void upSku(Long skuId);

    void downSku(Long skuId);

    List<Long> getSkuIds();

    /**
     * 修改sku
     * @param skuInfo
     */
    public void updateSkuInfo(SkuInfo skuInfo);

    BigDecimal getSkuPrice(Long skuId);
}
