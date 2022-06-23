package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lxstart
 * @description
 * @create 2022-06-22 16:32
 */

@RestController
@RequestMapping("/admin/product")
public class TrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * page：第几页
     * limit：每页数量
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page") Long page,@PathVariable("limit") Long limit){

        Page<BaseTrademark> p = new Page<>(page,limit);
        // 分页查询
        Page<BaseTrademark> trademarkPage = baseTrademarkService.page(p);
        return Result.ok(trademarkPage);

    }

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeTrademark(@PathVariable("id") Long id){

        baseTrademarkService.removeById(id);
        return Result.ok();

    }

    /**
     * 查找品牌信息
     * @param id
     * @return
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getTrademarkById(@PathVariable("id") Long id){

        BaseTrademark trademark = baseTrademarkService.getById(id);
        return  Result.ok(trademark);

    }

    /**
     * 添加一个品牌信息
     * @param baseTrademark
     * @return
     */
    @PostMapping("/baseTrademark/save")
    public Result saveTrademark(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @PutMapping("/baseTrademark/update")
    public Result updateTrademark(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }
}
