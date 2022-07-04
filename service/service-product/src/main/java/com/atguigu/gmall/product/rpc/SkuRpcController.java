package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.product.biz.SpudeSkuSaleAttrBizService;
import com.atguigu.gmall.product.service.BaseCategoryViewService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@RestController
@RequestMapping("/rpc/inner/product")
public class SkuRpcController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    BaseCategoryViewService baseCategoryViewService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SpudeSkuSaleAttrBizService spudeSkuSaleAttrBizService;

    @GetMapping("/skuinfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId) {
        SkuInfo info = skuInfoService.getById(skuId);
        return Result.ok(info);
    }

    /**
     * 根据三级分类id去 base_category_view 视图中查询精准分类路径
     *
     * @param c3Id
     * @return
     */
    @GetMapping("/categoryview/{c3Id}")
    public Result<CategoryView> getCategoryViewDo(@PathVariable("c3Id") Long c3Id) {

        CategoryViewDo categoryViewDo = baseCategoryViewService.getViewByC3Id(c3Id);

        //把dto转换成vo
        CategoryView categoryView = new CategoryView();
        categoryView.setCategory1Id(categoryViewDo.getCategory1Id());
        categoryView.setCategory1Name(categoryViewDo.getCategory1Name());
        categoryView.setCategory2Id(categoryViewDo.getCategory2Id());
        categoryView.setCategory2Name(categoryViewDo.getCategory2Name());
        categoryView.setCategory3Id(categoryViewDo.getCategory3Id());
        categoryView.setCategory3Name(categoryViewDo.getCategory3Name());

        return Result.ok(categoryView);
    }

        /**
         * 根据skuId和spuId查询出当前商品spu定义的所有销售属性名和值以及标记出当前sku是哪一对组合
         * @param skuId
         * @param spuId
         * @return
         */
        @GetMapping("/sku/saleattr/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSaleAttr(@PathVariable("skuId")Long skuId,
                                                 @PathVariable("spuId")Long spuId){

            List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrAndMarkSkuSaleValue(skuId,spuId);
            return Result.ok(list);
        }

    /**
     * 查出这个sku对应的spu到底有多少个sku组合，以及每个sku销售属性值组合封装成Map（"值1|值2|值N":skuId）
     */
    @GetMapping("/spu/skus/saleattrvalue/json/{spuId}")
    public Result<String> getSpudeAllSkuSaleAttrAndValue(@PathVariable("spuId") Long spuId){
        String json =spudeSkuSaleAttrBizService.getSpudeAllSkuSaleAttrAndValue(spuId);
        return Result.ok(json);
    }

    @GetMapping("/sku/price/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price = skuInfoService.getSkuPrice(skuId);
        return Result.ok(price);
    }
}
