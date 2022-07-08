package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lxstart
 * @description:
 * @create: 2022-06-26
 */
@RestController
@RequestMapping("/rpc/inner/item")
public class ItemRpcController {

    @Autowired
    ItemService itemService;
    /**
     * 根据skuId查询商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/sku/{skuId}")
    public Result<SkuDetailVo> getSkuDetail(@PathVariable("skuId") Long skuId){
        SkuDetailVo skuDetailVo = itemService.getSkuDetail(skuId);
        itemService.incrHotScore(skuId);
        return Result.ok(skuDetailVo);
    }


}
